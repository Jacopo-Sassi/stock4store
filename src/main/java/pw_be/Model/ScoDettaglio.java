package pw_be.Model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sco_dettaglio_sto")
public class ScoDettaglio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer contatore = 0;

    @Column(nullable = false)
    private Integer numero = 0;

    @Column(nullable = false)
    private LocalDateTime dataora;

    @Column(nullable = false, length = 10)
    private String codnegozio;

    @Column(nullable = false, length = 5)
    private String codpostazione = "";

    @Column(nullable = false)
    private Integer riga = 0;

    @Column(length = 50)
    private String codarticolo;

    @Column(nullable = false, length = 20)
    private String ean = "";

    @Column(name = "descri_articolo", nullable = false, length = 100)
    private String descriArticolo = "";

    @Column(nullable = false, length = 10)
    private String quantita = "";

    @Column(nullable = false, length = 15)
    private String scontoperc = "0";

    @Column(nullable = false, length = 15)
    private String scontoimp = "0,00";

    @Column(nullable = false, length = 15)
    private String prezzolistino = "0,00";

    @Column(nullable = false, length = 15)
    private String prezzomodificato = "0,00";

    @Column(nullable = false, length = 15)
    private String importo = "0,00";

    @Column(nullable = false, length = 10)
    private String iva = "";

    @Column(name = "tipo_mov", nullable = false, length = 1)
    private String tipoMov = "";

    @Column(nullable = false, length = 15)
    private String scontoperctestata = "0,00";

    @Column(nullable = false, length = 15)
    private String scontoimptestata = "0,00";

    @Column(nullable = false, length = 15)
    private String scontocalc = "0,00";

    @Column(nullable = false, length = 15)
    private String imposta = "0,00";

    @Column(nullable = false, length = 15)
    private String imponibile = "0,00";

    @Column(nullable = false, length = 45)
    private String codvenditore = "";

    @Column(name = "old_codarticolo", length = 50)
    private String oldCodarticolo;

    @Column(name = "old_descri_art", nullable = false, length = 100)
    private String oldDescriArt = "";

    @Column(nullable = false, length = 1)
    private String modificato = "";

    @Column(nullable = false, length = 1)
    private String annullato = "";

    @Column(nullable = false)
    private Integer contatoreordcli = 0;

    @Column(nullable = false, length = 45)
    private String voucher = "";

    @Column(nullable = false, length = 1)
    private String inviatoalserverone;

    @Column(nullable = false)
    private LocalDateTime dataultimamodifica;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        dataultimamodifica = LocalDateTime.now();
    }
}
