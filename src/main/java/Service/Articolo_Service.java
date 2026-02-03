package Service;

import Model.Articolo;
import Repository.Articoli_Repository;
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
        return articoliRepository.findByEan(ean);
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
}
