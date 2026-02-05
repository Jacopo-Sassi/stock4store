package pw_be.Repository;

import pw_be.Model.Ordine;
import pw_be.model.dto.StatoOrdineDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Ordini_Repository extends JpaRepository<Ordine, Long> {

    Optional<Ordine> findByNumeroOrdine(String numeroOrdine);

    List<Ordine> findByStato(StatoOrdineDto stato);

    List<Ordine> findByFornitoreId(Long fornitoreId);
}
