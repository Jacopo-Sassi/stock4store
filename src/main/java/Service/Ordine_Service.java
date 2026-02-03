// java
// File: src/main/java/Service/OrdineService.java
package Service;

import Model.Ordine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Ordine_Service {

    @Autowired
    private JpaRepository<Ordine, Long> ordineRepository;

    public List<Ordine> findAll() {
        return ordineRepository.findAll();
    }

    public Optional<Ordine> findById(Long id) {
        return ordineRepository.findById(id);
    }

    public Ordine save(Ordine ordine) {
        return ordineRepository.save(ordine);
    }

    public void delete(Long id) {
        ordineRepository.deleteById(id);
    }

    public long count() {
        return ordineRepository.count();
    }
}
