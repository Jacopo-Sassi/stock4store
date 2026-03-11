package pw_be.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pw_be.Model.Articolo_Stock;

import java.util.Optional;

@Repository
public interface Articolo_Stock_Repository extends JpaRepository<Articolo_Stock, Long> {
    Optional<Articolo_Stock> findByCodiceArticolo(String codiceArticolo);
}
