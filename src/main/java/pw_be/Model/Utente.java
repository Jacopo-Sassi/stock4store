package pw_be.Model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "utenti")
@Data
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_utente", nullable = false, unique = true, length = 50)
    private String nomeUtente;

    @Column(nullable = false, length = 64)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 20)
    private String ruolo;

    @Column(nullable = false)
    private Boolean attivo = true;

    @Column(name = "data_creazione", nullable = false, updatable = false)
    private LocalDateTime dataCreazione;

    @Column(name = "ultimo_accesso")
    private LocalDateTime ultimoAccesso;

    @PrePersist
    protected void onCreate() {
        dataCreazione = LocalDateTime.now();
    }
}
