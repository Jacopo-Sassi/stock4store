package pw_be.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "ordini")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_ordine", unique = true, nullable = false, length = 50)
    private String numeroOrdine;

    @Column(nullable = false, length = 50)
    private String stato;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totale;

    @Column(name = "data_ordine")
    private LocalDateTime dataOrdine;

    @Column(name = "fornitore_id", nullable = false)
    private Long fornitoreId;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ordine_Item> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        dataOrdine = LocalDateTime.now();
    }
}
