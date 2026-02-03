package Repository;

import Model.Attivita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface Attivita_Repository extends JpaRepository<Attivita, Long> {

    List<Attivita> findByTipo(String tipo);

    @Query("SELECT a FROM Attivita a ORDER BY a.timestamp DESC")
    List<Attivita> findAllOrderByTimestampDesc();

    @Query("SELECT a FROM Attivita a WHERE a.timestamp BETWEEN :dataInizio AND :dataFine ORDER BY a.timestamp DESC")
    List<Attivita> findByTimestampBetween(
            @Param("dataInizio") LocalDateTime dataInizio,
            @Param("dataFine") LocalDateTime dataFine
    );

    @Query(value = "SELECT a FROM Attivita a ORDER BY a.timestamp DESC LIMIT :limit", nativeQuery = false)
    List<Attivita> findTopByOrderByTimestampDesc(@Param("limit") int limit);
}
