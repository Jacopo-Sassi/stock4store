package pw_be.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "articolo_stock")
public class Articolo_Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codice_articolo", nullable = false, unique = true, length = 50)
    private String codiceArticolo;

    @Column(name = "quantita_stock", nullable = false)
    private Integer quantitaStock = 0;

    @UpdateTimestamp
    @Column(name = "data_aggiornamento", updatable = true)
    private LocalDateTime dataAggiornamento;

    // Relazione verso Articolo (opzionale ma consigliata)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codice_articolo", insertable = false, updatable = false)
    private Articolo articolo;
}
