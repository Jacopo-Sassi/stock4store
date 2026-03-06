package pw_be.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fornitori")
public class Fornitore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(name = "partita_iva", unique = true, nullable = false, length = 20)
    private String partitaIva;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 50)
    private String telefono;

    @Column(length = 500)
    private String indirizzo;

    @Column(length = 100)
    private String citta;

    @Column(length = 10)
    private String cap;

    @Column(length = 100)
    private String paese;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
