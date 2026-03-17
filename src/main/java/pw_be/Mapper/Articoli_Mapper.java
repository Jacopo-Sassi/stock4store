package pw_be.Mapper;

import org.example.pw_be.model.dto.ArticoloDto;
import org.example.pw_be.model.dto.ArticoloRequestDto;
import pw_be.Model.Articolo;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Mapper(componentModel = "spring")
public interface Articoli_Mapper {

    // =========================
    // ENTITY → DTO
    // =========================
    ArticoloDto toDto(Articolo entity);
    List<ArticoloDto> toDtoList(List<Articolo> entities);

    // =========================
    // REQUEST → ENTITY (CREATE)
    // =========================

    // campi presenti nel DTO → source
    @Mapping(target = "codice",          source = "codice")
    @Mapping(target = "descrizione",     source = "descrizione")
    @Mapping(target = "ean",             constant = "")
    @Mapping(target = "gruppo",          source = "gruppo")
    @Mapping(target = "stato",           constant = "ST")
    @Mapping(target = "lineaprod",       constant = "")
    @Mapping(target = "stagione",        constant = "PERMANENTE")
    @Mapping(target = "linkimmagine",    constant = "")
    @Mapping(target = "scodescri",       constant = "")
    @Mapping(target = "tipo",            constant = "ST")
    @Mapping(target = "iva",             constant = "")
    @Mapping(target = "codfornitore",    source = "codfornitore")
    @Mapping(target = "peso",            source = "peso")
    @Mapping(target = "note",            constant = "")
    @Mapping(target = "ubicazione",      constant = "")
    @Mapping(target = "ordinabile",      constant = "S")
    @Mapping(target = "prezzodilistino", source = "prezzodilistino")
    @Mapping(target = "scortaminima",    source = "scortaminima")
    @Mapping(target = "onlineRelevant",  constant = "1")

    // campi NON presenti nel DTO → constant o ignore
    @Mapping(target = "bidone",       constant = "")
    @Mapping(target = "codaccessori", constant = "")
    @Mapping(target = "grcassa",      constant = "")
    @Mapping(target = "codIniziale",  constant = "")
    @Mapping(target = "var1",         constant = "")
    @Mapping(target = "var2",         constant = "")
    @Mapping(target = "var3",         constant = "")
    @Mapping(target = "var4",         constant = "")
    @Mapping(target = "cursoremod",   constant = "")
    @Mapping(target = "gestgiacenza", constant = "")
    @Mapping(target = "codice2",      constant = "")
    @Mapping(target = "confezione",   constant = "")
    @Mapping(target = "GCliente",     constant = "")
    @Mapping(target = "qtafiglio",    constant = "0")
    @Mapping(target = "progre",       constant = "0")

    // lp opzionali → ignore (nullable in DB)
    @Mapping(target = "lp1", ignore = true)
    @Mapping(target = "lp2", ignore = true)
    @Mapping(target = "lp3", ignore = true)
    @Mapping(target = "lp4", ignore = true)
    @Mapping(target = "lp5", ignore = true)

    // nullable/gestiti DB
    @Mapping(target = "codpadre",           ignore = true)
    @Mapping(target = "datainserimento",    ignore = true)
    @Mapping(target = "dataritiro",         ignore = true)
    @Mapping(target = "datascad",           ignore = true)
    @Mapping(target = "dataultimamodifica", ignore = true)

    // read-only
    @Mapping(target = "quantitaStock", ignore = true)

    Articolo toEntity(ArticoloRequestDto requestDto);

    // =========================
    // UPDATE
    // =========================
    @Mapping(target = "codice",             ignore = true)
    @Mapping(target = "datainserimento",    ignore = true)
    @Mapping(target = "dataritiro",         ignore = true)
    @Mapping(target = "datascad",           ignore = true)
    @Mapping(target = "dataultimamodifica", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "quantitaStock",      ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ArticoloRequestDto requestDto, @MappingTarget Articolo entity);

    // =========================
    // CONVERSIONI DATE
    // =========================
    default OffsetDateTime map(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) return null;
        return offsetDateTime.toLocalDateTime();
    }
}
