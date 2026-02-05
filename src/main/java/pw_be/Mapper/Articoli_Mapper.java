package pw_be.Mapper;

import pw_be.Model.Articolo;
import pw_be.model.dto.ArticoloDto;
import pw_be.model.dto.ArticoloRequestDto;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
public class Articoli_Mapper {

    // DTO Request -> Entity
    public Articolo toEntity(ArticoloRequestDto request) {
        Articolo articolo = new Articolo();

        articolo.setCodice(request.getCodice());
        articolo.setDescrizione(request.getDescrizione() != null ? request.getDescrizione() : "");
        articolo.setEan(request.getEan() != null ? request.getEan() : "");
        articolo.setGruppo(request.getGruppo());
        articolo.setStato(request.getStato());
        articolo.setLp1(request.getLp1());
        articolo.setLp2(request.getLp2());
        articolo.setLp3(request.getLp3());
        articolo.setLp4(request.getLp4());
        articolo.setLp5(request.getLp5() != null ? request.getLp5() : "");
        articolo.setLineaprod(request.getLineaprod() != null ? request.getLineaprod() : "");
        articolo.setStagione(request.getStagione());
        articolo.setLinkimmagine(request.getLinkimmagine());
        articolo.setScodescri(request.getScodescri());
        articolo.setTipo(request.getTipo());
        articolo.setIva(request.getIva());
        articolo.setCodfornitore(request.getCodfornitore());
        articolo.setPeso(request.getPeso());
        articolo.setNote(request.getNote() != null ? request.getNote() : "");
        articolo.setUbicazione(request.getUbicazione());
        articolo.setDatainserimento(LocalDateTime.now());
        return articolo;
    }

    // Entity -> DTO Response
    public ArticoloDto toDto(Articolo articolo) {
        ArticoloDto dto = new ArticoloDto();

        dto.setCodice(articolo.getCodice());
        dto.setDescrizione(articolo.getDescrizione());
        dto.setEan(articolo.getEan());
        dto.setGruppo(articolo.getGruppo());
        dto.setStato(articolo.getStato());
        dto.setLp1(articolo.getLp1());
        dto.setLp2(articolo.getLp2());
        dto.setLp3(articolo.getLp3());
        dto.setLp4(articolo.getLp4());
        dto.setLp5(articolo.getLp5());
        dto.setLineaprod(articolo.getLineaprod());
        dto.setStagione(articolo.getStagione());
        dto.setLinkimmagine(articolo.getLinkimmagine());
        dto.setBidone(articolo.getBidone());
        dto.setScodescri(articolo.getScodescri());
        dto.setTipo(articolo.getTipo());
        dto.setIva(articolo.getIva());
        dto.setCodfornitore(articolo.getCodfornitore());
        dto.setPeso(articolo.getPeso());
        dto.setNote(articolo.getNote());
        dto.setUbicazione(articolo.getUbicazione());
        dto.setScortaminima(articolo.getScortaminima());
        dto.setCodpadre(articolo.getCodpadre());
        dto.setQtafiglio(articolo.getQtafiglio());
        dto.setCodaccessori(articolo.getCodaccessori());
        dto.setGrcassa(articolo.getGrcassa());
        dto.setOrdinabile(articolo.getOrdinabile());
        dto.setCodIniziale(articolo.getCodIniziale());
        dto.setVar1(articolo.getVar1());
        dto.setVar2(articolo.getVar2());
        dto.setVar3(articolo.getVar3());
        dto.setVar4(articolo.getVar4());
        dto.setCursoremod(articolo.getCursoremod());
        dto.setGestgiacenza(articolo.getGestgiacenza());
        dto.setCodice2(articolo.getCodice2());
        dto.setConfezione(articolo.getConfezione());
        dto.setProgre(articolo.getProgre());
        dto.setOnlineRelevant(articolo.getOnlineRelevant());

        return dto;
    }
}
