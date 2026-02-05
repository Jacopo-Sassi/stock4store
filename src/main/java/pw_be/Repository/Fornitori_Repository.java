package pw_be.Repository;

import pw_be.Model.Fornitore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Fornitori_Repository extends JpaRepository<Fornitore, Long> {

    Optional<Fornitore> findByEmail(String email);

    Optional<Fornitore> findByPartitaIva(String partitaIva);

    @Query("SELECT f FROM Fornitore f WHERE " +
            "LOWER(f.nome) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.partitaIva) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Fornitore> searchFornitori(@Param("query") String query);

    List<Fornitore> findByCitta(String citta);
}
