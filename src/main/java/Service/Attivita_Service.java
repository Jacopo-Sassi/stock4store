
package Service;

import Model.Attivita;
import Repository.Attivita_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Attivita_Service {

    @Autowired
    private Attivita_Repository attivitaRepository;

    public List<Attivita> findAll() {
        return attivitaRepository.findAll();
    }

    public Optional<Attivita> findById(Long id) {
        return attivitaRepository.findById(id);
    }

    public List<Attivita> findByTipo(String tipo) {
        return attivitaRepository.findByTipo(tipo);
    }

    public List<Attivita> findAllOrderByTimestampDesc() {
        return attivitaRepository.findAllOrderByTimestampDesc();
    }

    public List<Attivita> findByTimestampBetween(LocalDateTime dataInizio, LocalDateTime dataFine) {
        return attivitaRepository.findByTimestampBetween(dataInizio, dataFine);
    }

    public List<Attivita> findTopByOrderByTimestampDesc(int limit) {
        return attivitaRepository.findTopByOrderByTimestampDesc(limit);
    }

    public Attivita save(Attivita attivita) {
        return attivitaRepository.save(attivita);
    }

    public Attivita logAttivita(String tipo, String descrizione) {
        Attivita attivita = new Attivita();
        attivita.setTipo(tipo);
        attivita.setDescrizione(descrizione);
        attivita.setTimestamp(LocalDateTime.now());
        return attivitaRepository.save(attivita);
    }

    public void delete(Long id) {
        attivitaRepository.deleteById(id);
    }

    public long count() {
        return attivitaRepository.count();
    }
}
