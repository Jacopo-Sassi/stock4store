package pw_be.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import pw_be.Model.Articolo;
import pw_be.Repository.Articoli_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Articolo_Service {

    @Autowired
    private Articoli_Repository articoliRepository;

    public List<Articolo> findAll() {
        return articoliRepository.findAll();
    }

    public Optional<Articolo> findByCodice(String codice) {
        return articoliRepository.findById(codice);
    }

    public Optional<Articolo> findByEan(String ean) {
        return Optional.ofNullable(articoliRepository.findByEan(ean));
    }

    public List<Articolo> findByGruppo(String gruppo) {
        return articoliRepository.findByGruppo(gruppo);
    }

    public List<Articolo> findByStato(String stato) {
        return articoliRepository.findByStato(stato);
    }

    public List<Articolo> searchArticoli(String query) {
        return articoliRepository.searchArticoli(query);
    }

    public List<Articolo> findByPrezzoRange(BigDecimal min, BigDecimal max) {
        return articoliRepository.findByPrezzodilistinoBetween(min, max);
    }

    public List<Articolo> findByStagione(String stagione) {
        return articoliRepository.findByStagione(stagione);
    }

    public List<Articolo> findByScortaMinimaLessThan(Integer quantita) {
        return articoliRepository.findByScortaminimaLessThan(quantita);
    }

    public Articolo save(Articolo articolo) {
        return articoliRepository.save(articolo);
    }

    public void delete(String codice) {
        articoliRepository.deleteById(codice);
    }

    public boolean exists(String codice) {
        return articoliRepository.existsById(codice);
    }

    public long count() {
        return articoliRepository.count();
    }

    public List<Articolo> getArticoli(String gruppo, String stato, String stagione) {
        if (gruppo != null) {
            return articoliRepository.findByGruppo(gruppo);
        } else if (stato != null) {
            return articoliRepository.findByStato(stato);
        } else if (stagione != null) {
            return articoliRepository.findByStagione(stagione);
        } else {
            return articoliRepository.findAll();
        }
    }

    public int getQuantitaDisponibile(String codice) {
        articoliRepository.findById(codice)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Articolo con codice " + codice + " non trovato"
                ));

        Integer quantita = articoliRepository.getQuantitaDisponibile(codice);
        return quantita != null ? quantita : 0;
    }


}
