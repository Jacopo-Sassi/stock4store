
package Service;

import Model.Gerarchia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Gerarchia_Service {

    @Autowired
    private JpaRepository<Gerarchia, Long> gerarchiaRepository;

    public List<Gerarchia> findAll() {
        return gerarchiaRepository.findAll();
    }

    public Optional<Gerarchia> findById(Long id) {
        return gerarchiaRepository.findById(id);
    }

    public Gerarchia save(Gerarchia gerarchia) {
        return gerarchiaRepository.save(gerarchia);
    }

    public void delete(Long id) {
        gerarchiaRepository.deleteById(id);
    }

    public long count() {
        return gerarchiaRepository.count();
    }
}
