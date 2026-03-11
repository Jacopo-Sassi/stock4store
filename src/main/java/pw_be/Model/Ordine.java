package pw_be.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "ordini")
public class Ordine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "numero_ordine",
            unique = true,
            nullable = false,
            length = 50
            // ← rimossi insertable = false e updatable = false
    )
    private String numeroOrdine;

    @Column(nullable = false, length = 50)
    private String stato;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totale;

    @CreationTimestamp
    @Column(name = "data_ordine", updatable = false)
    private LocalDateTime dataOrdine;

    @Column(name = "fornitore_id", nullable = false)
    private Long fornitoreId;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ordine_Item> items = new ArrayList<>();

    @PrePersist
    public void generaNumeroOrdine() {
        if (this.numeroOrdine == null) {
            this.numeroOrdine = "ORD-"
                    + java.time.LocalDate.now()
                    .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE)
                    + "-"
                    + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
