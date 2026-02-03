package Model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "gerarchie")
public class Gerarchia{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3, nullable = false)
    private String lp1 = "";

    @Column(length = 3, nullable = false)
    private String lp2 = "";

    @Column(length = 3, nullable = false)
    private String lp3 = "";

    @Column(length = 3, nullable = false)
    private String lp4 = "";

    @Column(length = 3, nullable = false)
    private String lp5 = "";

    @Column(length = 100, nullable = false)
    private String descri1 = "";

    @Column(length = 250, nullable = false)
    private String descri2 = "";

    @Column(name = "g_cliente", length = 20, nullable = false)
    private String gCliente = "";

    @Column(nullable = false)
    private Integer progre = 0;

    @Column(nullable = false)
    private LocalDateTime dataultimamodifica;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        dataultimamodifica = LocalDateTime.now();
    }
}
