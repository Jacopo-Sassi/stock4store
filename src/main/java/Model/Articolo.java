package Model;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "articoli")
public class Articolo {

    @Id
    @Column(length = 50)
    private String codice;

    @Column(length = 100, nullable = false)
    private String descrizione = "";

    @Column(length = 13, nullable = false)
    private String ean = "";

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzodilistino;

    @Column(length = 15, nullable = false)
    private String gruppo;

    @Column(length = 2, nullable = false)
    private String stato;

    @Column(length = 3)
    private String lp1;

    @Column(length = 3)
    private String lp2;

    @Column(length = 3)
    private String lp3;

    @Column(length = 3)
    private String lp4;

    @Column(length = 3)
    private String lp5 = "";

    @Column(length = 100, nullable = false)
    private String lineaprod = "";

    @Column(length = 50, nullable = false)
    private String stagione;

    @Column(length = 200, nullable = false)
    private String linkimmagine;

    @Column(length = 15, nullable = false)
    private String bidone;

    @Column(length = 15, nullable = false)
    private String scodescri;

    @Column(length = 2, nullable = false)
    private String tipo;

    @Column(length = 4, nullable = false)
    private String iva;

    @Column(length = 45, nullable = false)
    private String codfornitore;

    @Column(length = 10, nullable = false)
    private String peso;

    @Column(length = 250, nullable = false)
    private String note;

    @Column(length = 45, nullable = false)
    private String ubicazione;

    private LocalDateTime datainserimento;

    private LocalDateTime dataritiro;

    @Column(nullable = false)
    private Integer scortaminima = 0;

    @Column(length = 50)
    private String codpadre;

    @Column(nullable = false)
    private Integer qtafiglio = 0;

    @Column(length = 45, nullable = false)
    private String codaccessori;

    @Column(length = 10, nullable = false)
    private String grcassa;

    @Column(length = 1, nullable = false)
    private String ordinabile = "";

    private LocalDateTime datascad;

    @Column(name = "cod_iniziale", length = 30, nullable = false)
    private String codIniziale = "";

    @Column(length = 10, nullable = false)
    private String var1 = "";

    @Column(length = 10, nullable = false)
    private String var2 = "";

    @Column(length = 10, nullable = false)
    private String var3 = "";

    @Column(length = 10, nullable = false)
    private String var4 = "";

    @Column(length = 1, nullable = false)
    private String cursoremod = "";

    @Column(length = 1, nullable = false)
    private String gestgiacenza = "";

    @Column(length = 50, nullable = false)
    private String codice2 = "";

    @Column(length = 1, nullable = false)
    private String confezione = "";

    @Column(name = "g_cliente", length = 20, nullable = false)
    private String gCliente = "";

    @Column(nullable = false)
    private Integer progre;

    @Column(nullable = false)
    private LocalDateTime dataultimamodifica;

    @Column(name = "online_relevant", nullable = false)
    private Integer onlineRelevant = 0;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        dataultimamodifica = LocalDateTime.now();
    }
}
