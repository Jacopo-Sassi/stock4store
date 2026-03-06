package pw_be.Repository;

import pw_be.Model.Ordine_Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Ordini_Items_Repository extends JpaRepository<Ordine_Item, Long> {

    List<Ordine_Item> findByOrdineId(Long ordineId);

    List<Ordine_Item> findByCodiceArticolo(String codiceArticolo);

    @Query("SELECT oi FROM Ordine_Item oi WHERE oi.ordine.id = :ordineId")
    List<Ordine_Item> findItemsByOrdineId(@Param("ordineId") Long ordineId);
}
