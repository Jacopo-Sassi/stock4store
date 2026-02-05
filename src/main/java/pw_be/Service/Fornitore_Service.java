
package pw_be.Service;

import pw_be.Model.Fornitore;
import pw_be.Repository.Fornitori_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Fornitore_Service {

    @Autowired
    private Fornitori_Repository fornitoriRepository;

    public List<Fornitore> findAll() {
        return fornitoriRepository.findAll();
    }

    public Optional<Fornitore> findById(Long id) {
        return fornitoriRepository.findById(id);
    }

    public Optional<Fornitore> findByEmail(String email) {
        return fornitoriRepository.findByEmail(email);
    }

    public Optional<Fornitore> findByPartitaIva(String partitaIva) {
        return fornitoriRepository.findByPartitaIva(partitaIva);
    }

    public List<Fornitore> searchFornitori(String query) {
        return fornitoriRepository.searchFornitori(query);
    }

    public List<Fornitore> findByCitta(String citta) {
        return fornitoriRepository.findByCitta(citta);
    }

    public Fornitore save(Fornitore fornitore) {
        return fornitoriRepository.save(fornitore);
    }

    public void delete(Long id) {
        fornitoriRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return fornitoriRepository.existsById(id);
    }

    public long count() {
        return fornitoriRepository.count();
    }
}
