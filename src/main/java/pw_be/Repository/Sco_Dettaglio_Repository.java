package pw_be.Repository;
import pw_be.Model.ScoDettaglio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface Sco_Dettaglio_Repository extends JpaRepository<ScoDettaglio, Long> {

    List<ScoDettaglio> findByContatore(Integer contatore);

    List<ScoDettaglio> findByCodarticolo(String codarticolo);

    @Query("SELECT s FROM ScoDettaglio s WHERE s.dataora BETWEEN :dataInizio AND :dataFine")
    List<ScoDettaglio> findByDataoraBetween(
            @Param("dataInizio") LocalDateTime dataInizio,
            @Param("dataFine") LocalDateTime dataFine
    );

    List<ScoDettaglio> findByCodnegozio(String codnegozio);

    @Query("SELECT s FROM ScoDettaglio s WHERE s.tipoMov = :tipoMov")
    List<ScoDettaglio> findByTipoMov(@Param("tipoMov") String tipoMov);
}
