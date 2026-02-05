package pw_be.Repository;

import pw_be.Model.Articolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface Articoli_Repository extends JpaRepository<Articolo, String> {

    Optional<Articolo> findByCodice(String codice);

    Articolo findByEan(String ean);

    List<Articolo> findByGruppo(String gruppo);

    List<Articolo> findByStato(String stato);

    List<Articolo> findByStagione(String stagione);

    List<Articolo> findByGruppoAndStato(String gruppo, String stato);

    @Query("SELECT a FROM Articolo a WHERE " +
            "LOWER(a.descrizione) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.codice) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.ean) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(a.scodescri) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Articolo> searchArticoli(@Param("query") String query);

    @Query("SELECT a FROM Articolo a WHERE " +
            "(:gruppo IS NULL OR a.gruppo = :gruppo) AND " +
            "(:stato IS NULL OR a.stato = :stato) AND " +
            "(:stagione IS NULL OR a.stagione = :stagione) AND " +
            "(:prezzoMin IS NULL OR a.prezzodilistino >= :prezzoMin) AND " +
            "(:prezzoMax IS NULL OR a.prezzodilistino <= :prezzoMax)")
    List<Articolo> filterArticoli(
            @Param("gruppo") String gruppo,
            @Param("stato") String stato,
            @Param("stagione") String stagione,
            @Param("prezzoMin") BigDecimal prezzoMin,
            @Param("prezzoMax") BigDecimal prezzoMax
    );

    List<Articolo> findByCodfornitore(String codfornitore);

    List<Articolo> findByLineaprod(String lineaprod);

    @Query("SELECT a FROM Articolo a WHERE a.onlineRelevant = 1")
    List<Articolo> findArticoliOnline();

    @Query("SELECT COUNT(a) FROM Articolo a WHERE a.gruppo = :gruppo")
    Long countByGruppo(@Param("gruppo") String gruppo);

    List<Articolo> findByPrezzodilistinoBetween(BigDecimal min, BigDecimal max);

    List<Articolo> findByScortaminimaLessThan(Integer quantita);
}
