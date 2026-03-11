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
        Ordine ordine = ordiniMapper.toEntity(ordineRequestDto);
        ordine.setStato("CREATO");

        if (ordineRequestDto.getArticoli() != null && !ordineRequestDto.getArticoli().isEmpty()) {
            List<Ordine_Item> items = ordineRequestDto.getArticoli().stream()
                    .map(itemDto -> {
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
                        item.setPrezzoUnitario(BigDecimal.ZERO);
                        item.setSubtotale(BigDecimal.ZERO);
                        item.setOrdine(ordine);
                        return item;
                    })
                    .toList();
            ordine.getItems().addAll(items);
        }

        BigDecimal totale = ordine.getItems().stream()
                .map(Ordine_Item::getSubtotale)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ordine.setTotale(totale);

        return ordiniMapper.toDto(ordineRepository.save(ordine));
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
