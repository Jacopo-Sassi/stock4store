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

    // Entity → DTO (per risposta API)
    ArticoloDto toDto(Articolo entity);

    // RequestDTO → Entity (per creazione)
    @Mapping(target = "codice", ignore = true)
    @Mapping(target = "datainserimento", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "descrizione", defaultValue = "")
    @Mapping(target = "ean", defaultValue = "")
    @Mapping(target = "lp5", defaultValue = "")
    @Mapping(target = "lineaprod", defaultValue = "")
    @Mapping(target = "note", defaultValue = "")
    Articolo toEntity(ArticoloRequestDto requestDto);

    // Update Entity da RequestDTO (per aggiornamento)
    @Mapping(target = "codice", ignore = true)
    @Mapping(target = "datainserimento", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ArticoloRequestDto requestDto, @MappingTarget Articolo entity);

    // List mapping
    List<ArticoloDto> toDtoList(List<Articolo> entities);

    // Conversione LocalDateTime → OffsetDateTime
    default OffsetDateTime map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    // Conversione OffsetDateTime → LocalDateTime
    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.toLocalDateTime();
    }
}
