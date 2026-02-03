
package Service;

import Model.Ordine_Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class Ordine_Item_Service {

    @Autowired
    private JpaRepository<Ordine_Item, Long> ordineItemRepository;

    public List<Ordine_Item> findAll() {
        return ordineItemRepository.findAll();
    }

    public Optional<Ordine_Item> findById(Long id) {
        return ordineItemRepository.findById(id);
    }

    public Ordine_Item save(Ordine_Item ordineItem) {
        return ordineItemRepository.save(ordineItem);
    }

    public void delete(Long id) {
        ordineItemRepository.deleteById(id);
    }

    public long count() {
        return ordineItemRepository.count();
    }
}
