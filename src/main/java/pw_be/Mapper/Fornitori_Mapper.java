package pw_be.Mapper;

import org.example.pw_be.model.dto.FornitoreDto;
import org.example.pw_be.model.dto.FornitoreRequestDto;
import pw_be.Model.Fornitore;

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
public interface Fornitori_Mapper {

    // Entity → DTO (per risposta API)
    FornitoreDto toDto(Fornitore entity);

    // RequestDTO → Entity (per creazione)
    @Mapping(target = "id", ignore = true)
    Fornitore toEntity(FornitoreRequestDto requestDto);

    // Update Entity da RequestDTO (per aggiornamento)
    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(FornitoreRequestDto requestDto, @MappingTarget Fornitore entity);

    // List mapping
    List<FornitoreDto> toDtoList(List<Fornitore> entities);

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
