package pw_be.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pw_be.Model.Gerarchia;

@Repository
public interface Gerarchia_Repository extends JpaRepository<Gerarchia, Long> {

}
