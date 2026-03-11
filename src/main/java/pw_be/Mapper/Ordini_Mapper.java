package pw_be.Mapper;

import org.example.pw_be.model.dto.OrdineDto;
import org.example.pw_be.model.dto.OrdineItemDto;
import org.example.pw_be.model.dto.OrdineRequestDto;
import pw_be.Model.Ordine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pw_be.Model.Ordine_Item;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface Ordini_Mapper {

    @Mapping(target = "numeroOrdine", ignore = true)
    @Mapping(target = "dataOrdine", ignore = true)
    @Mapping(target = "stato", ignore = true)
    @Mapping(target = "totale", ignore = true)
    @Mapping(target = "items", ignore = true)
    Ordine toEntity(OrdineRequestDto dto);

    @Mapping(source = "items", target = "articoli")  // ← mappa items → articoli
    @Mapping(source = "dataOrdine", target = "dataOrdine", qualifiedByName = "toOffsetDateTime")
    OrdineDto toDto(Ordine entity);

    // Mapping singolo item
    @Mapping(source = "codiceArticolo", target = "codiceArticolo")
    @Mapping(source = "quantita", target = "quantita")
    @Mapping(source = "prezzoUnitario", target = "prezzoUnitario")
    @Mapping(source = "subtotale", target = "subtotale")
    OrdineItemDto toItemDto(Ordine_Item item);

    @Named("toOffsetDateTime")
    default OffsetDateTime toOffsetDateTime(LocalDateTime ldt) {
        if (ldt == null) return null;
        return ldt.atOffset(ZoneOffset.UTC);
    }
}
