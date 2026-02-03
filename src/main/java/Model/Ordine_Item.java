package Model;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ordini_items")
public class Ordine_Item
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordine_id", nullable = false)
    private Ordine ordine;

    @Column(name = "codice_articolo", nullable = false, length = 50)
    private String codiceArticolo;

    @Column(nullable = false)
    private Integer quantita;

    @Column(name = "prezzo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotale;
}
