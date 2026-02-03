package Repository;

import Model.Ordine;
import org.example.pw_be.model.dto.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Ordini_Repository extends JpaRepository<Ordine, Long> {

    Optional<Ordine> findByNumeroOrdine(String numeroOrdine);

    List<Ordine> findByStato(StatoOrdine stato);

    List<Ordine> findByFornitoreId(Long fornitoreId);
}
