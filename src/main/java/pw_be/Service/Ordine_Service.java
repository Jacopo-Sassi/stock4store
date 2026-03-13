package pw_be.Service;

import org.example.pw_be.model.dto.OrdineDto;
import org.example.pw_be.model.dto.OrdineRequestDto;
import org.example.pw_be.model.dto.StatoOrdineDto;
import pw_be.Mapper.Ordini_Mapper;
import pw_be.Model.Articolo;
import pw_be.Model.Articolo_Stock;
import pw_be.Model.Ordine;
import pw_be.Model.Ordine_Item;
import pw_be.Repository.Articoli_Repository;
import pw_be.Repository.Articolo_Stock_Repository;
import pw_be.Repository.Ordini_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Ordine_Service {

    @Autowired
    private Ordini_Repository ordineRepository;

    @Autowired
    private Articoli_Repository articoliRepository;

    @Autowired
    private Articolo_Stock_Repository articoloStockRepository;  // ← aggiunto

    @Autowired
    private Ordini_Mapper ordiniMapper;

    public OrdineDto createOrdine(OrdineRequestDto ordineRequestDto) {
        // Mappo i campi base dell'ordine (fornitoreId, ecc.)
        Ordine ordine = ordiniMapper.toEntity(ordineRequestDto);
        ordine.setStato("CREATO");

        // Inizializzo sempre la lista, così non ho NPE
        if (ordine.getItems() == null) {
            ordine.setItems(new ArrayList<>());
        }

        if (ordineRequestDto.getArticoli() != null && !ordineRequestDto.getArticoli().isEmpty()) {

            // Se hai definito il mapping RequestItem -> Ordine_Item nel mapper:
            // List<Ordine_Item> items = ordiniMapper.toItemEntities(ordineRequestDto.getArticoli());

            // Se preferisci gestirlo qui (esplicito e chiaro):
            List<Ordine_Item> items = ordineRequestDto.getArticoli().stream()
                    .map(itemDto -> {
                        // Crea articolo se non esiste
                        if (!articoliRepository.existsById(itemDto.getCodiceArticolo())) {
                            Articolo nuovoArticolo = new Articolo();
                            nuovoArticolo.setCodice(itemDto.getCodiceArticolo());
                            nuovoArticolo.setDescrizione("Articolo " + itemDto.getCodiceArticolo());
                            nuovoArticolo.setStato("AT");
                            nuovoArticolo.setPrezzodilistino(BigDecimal.ZERO);
                            nuovoArticolo.setGruppo("N/D");
                            articoliRepository.save(nuovoArticolo);
                        }

                        Ordine_Item item = new Ordine_Item();
                        item.setCodiceArticolo(itemDto.getCodiceArticolo());
                        item.setQuantita(itemDto.getQuantita());

                        BigDecimal prezzo = itemDto.getPrezzoUnitario() != null
                                ? BigDecimal.valueOf(itemDto.getPrezzoUnitario())
                                : BigDecimal.ZERO;
                        item.setPrezzoUnitario(prezzo);

                        // subtotale sempre calcolato nel service
                        int qta = itemDto.getQuantita() != null ? itemDto.getQuantita() : 0;
                        BigDecimal subtotale = prezzo.multiply(BigDecimal.valueOf(qta));
                        item.setSubtotale(subtotale);

                        // relazione con Ordine
                        item.setOrdine(ordine);
                        return item;
                    })
                    .toList();

            ordine.getItems().addAll(items);
        }

        // Calcolo totale null-safe, ricalcolando di fatto la somma dei subtotali
        BigDecimal totale = ordine.getItems().stream()
                .map(item -> {
                    BigDecimal subtotale = item.getSubtotale();
                    if (subtotale == null) {
                        BigDecimal prezzo = item.getPrezzoUnitario() != null ? item.getPrezzoUnitario() : BigDecimal.ZERO;
                        int qta = item.getQuantita() != null ? item.getQuantita() : 0;
                        subtotale = prezzo.multiply(BigDecimal.valueOf(qta));
                        item.setSubtotale(subtotale);
                    }
                    return subtotale;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        ordine.setTotale(totale);

        // Salvo e ritorno DTO
        Ordine ordineSalvato = ordineRepository.save(ordine);
        return ordiniMapper.toDto(ordineSalvato);
    }

    public OrdineDto getOrdineById(Long id) {
        return ordineRepository.findById(id)
                .map(ordiniMapper::toDto)
                .orElse(null);
    }

    public List<OrdineDto> getOrdini(StatoOrdineDto stato) {
        if (stato == null) {
            return ordineRepository.findAll().stream()
                    .map(ordiniMapper::toDto)
                    .toList();
        }
        return ordineRepository.findByStato(stato).stream()
                .map(ordiniMapper::toDto)
                .toList();
    }

    public OrdineDto avanzaOrdine(Long id, StatoOrdineDto nuovoStato) {
        Ordine ordine = ordineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordine non trovato: " + id));

        String statoAttuale = ordine.getStato();
        ordine.setStato(nuovoStato.name());

        // Aggiorna articolo_stock quando CONSEGNATO
        if (nuovoStato == StatoOrdineDto.CONSEGNATO) {
            for (Ordine_Item item : ordine.getItems()) {
                Optional<Articolo_Stock> stockOpt =
                        articoloStockRepository.findByCodiceArticolo(item.getCodiceArticolo());

                if (stockOpt.isPresent()) {
                    // Record esiste → aggiorna quantita
                    Articolo_Stock stock = stockOpt.get();
                    stock.setQuantitaStock(stock.getQuantitaStock() + item.getQuantita());
                    articoloStockRepository.save(stock);
                } else {
                    // Record non esiste → crea nuovo
                    Articolo_Stock nuovoStock = new Articolo_Stock();
                    nuovoStock.setCodiceArticolo(item.getCodiceArticolo());
                    nuovoStock.setQuantitaStock(item.getQuantita());
                    articoloStockRepository.save(nuovoStock);
                }
            }
        }

        // Decrementa stock se ANNULLATO dopo CONSEGNATO
        if (nuovoStato == StatoOrdineDto.ANNULLATO && "CONSEGNATO".equals(statoAttuale)) {
            for (Ordine_Item item : ordine.getItems()) {
                articoloStockRepository.findByCodiceArticolo(item.getCodiceArticolo())
                        .ifPresent(stock -> {
                            int nuovoStock = stock.getQuantitaStock() - item.getQuantita();
                            stock.setQuantitaStock(Math.max(nuovoStock, 0)); // mai negativo
                            articoloStockRepository.save(stock);
                        });
            }
        }

        return ordiniMapper.toDto(ordineRepository.save(ordine));
    }

    public void deleteOrdine(Long id) {
        if (!ordineRepository.existsById(id)) {
            throw new RuntimeException("Ordine non trovato: " + id);
        }
        ordineRepository.deleteById(id);
    }

    public List<Ordine> findAll() { return ordineRepository.findAll(); }
    public Optional<Ordine> findById(Long id) { return ordineRepository.findById(id); }
    public Ordine save(Ordine ordine) { return ordineRepository.save(ordine); }
    public void delete(Long id) { ordineRepository.deleteById(id); }
    public long count() { return ordineRepository.count(); }
}
