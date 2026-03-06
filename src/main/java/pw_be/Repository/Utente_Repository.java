package pw_be.Repository;

import pw_be.Model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface Utente_Repository extends JpaRepository<Utente, Long> {
    Optional<Utente> findByNomeUtente(String nomeUtente);
    Optional<Utente> findByEmail(String email);
    boolean existsByNomeUtente(String nomeUtente);
    boolean existsByEmail(String email);
}
