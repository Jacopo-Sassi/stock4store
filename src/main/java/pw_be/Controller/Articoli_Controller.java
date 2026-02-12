package pw_be.Controller;

import org.example.pw_be.api.ArticoliApi;
import org.example.pw_be.model.dto.ArticoloDto;
import org.example.pw_be.model.dto.ArticoloRequestDto;
import org.example.pw_be.model.dto.CheckStock200ResponseDto;
import org.example.pw_be.model.dto.SearchRequestDto;
import pw_be.Mapper.Articoli_Mapper;
import pw_be.Model.Articolo;
import pw_be.Repository.Articoli_Repository;
import pw_be.Service.Articolo_Service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class Articoli_Controller implements ArticoliApi {

    private final Articoli_Repository articoliRepository;
    private final Articoli_Mapper articoliMapper;
    private final Articolo_Service articoloService;

    public Articoli_Controller(Articoli_Repository articoliRepository,
                               Articoli_Mapper articoliMapper,
                               Articolo_Service articoloService) {
        this.articoliRepository = articoliRepository;
        this.articoliMapper = articoliMapper;
        this.articoloService = articoloService;
    }


    @Override
    public ResponseEntity<CheckStock200ResponseDto> _checkStock(String codice) {
        int stock = articoloService.getQuantitaDisponibile(codice);
        CheckStock200ResponseDto response = new CheckStock200ResponseDto();
        response.setCodice(codice);
        response.setQuantitaDisponibile(stock);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ArticoloDto> _createArticolo(ArticoloRequestDto articoloRequestDto) {
        // Converti DTO -> Entity
        Articolo articolo = articoliMapper.toEntity(articoloRequestDto);

        // Salva nel database
        Articolo articoloSalvato = articoliRepository.save(articolo);

        // Converti Entity -> DTO per la response
        ArticoloDto responseDto = articoliMapper.toDto(articoloSalvato);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Override
    public ResponseEntity<Void> _deleteArticolo(String codice) {
        // Verifica se esiste
        if (!articoliRepository.existsById(codice)) {
            return ResponseEntity.notFound().build();
        }

        // Elimina
        articoliRepository.deleteById(codice);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<ArticoloDto>> _getArticoli(String gruppo, String stato, String stagione) {
        // Delega al service per la logica di filtro
        List<Articolo> articoli = articoloService.getArticoli(gruppo, stato, stagione);

        // Converti lista Entity -> lista DTO
        List<ArticoloDto> articoliDto = articoli.stream()
                .map(articoliMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(articoliDto);
    }

    @Override
    public ResponseEntity<ArticoloDto> _getArticoloByCodice(String codice) {
        return articoliRepository.findById(codice)
                .map(articoliMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<ArticoloDto> _getArticoloByEan(String ean) {
        Articolo articolo = articoliRepository.findByEan(ean);

        if (articolo == null) {
            return ResponseEntity.notFound().build();
        }

        ArticoloDto dto = articoliMapper.toDto(articolo);
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<List<ArticoloDto>> _searchArticoli(SearchRequestDto searchRequestDto) {
        // Delega al service per la logica di ricerca
        List<Articolo> articoli = articoloService.searchArticoli(String.valueOf(searchRequestDto));

        // Converti lista Entity -> lista DTO
        List<ArticoloDto> articoliDto = articoli.stream()
                .map(articoliMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(articoliDto);
    }

    @Override
    public ResponseEntity<ArticoloDto> _updateArticolo(String codice, ArticoloRequestDto articoloRequestDto) {
        // Verifica se l'articolo esiste
        return articoliRepository.findById(codice)
                .map(existingArticolo -> {
                    // Converti DTO -> Entity
                    Articolo updatedArticolo = articoliMapper.toEntity(articoloRequestDto);

                    // Mantieni lo stesso codice (chiave primaria)
                    updatedArticolo.setCodice(codice);

                    // Salva le modifiche
                    Articolo saved = articoliRepository.save(updatedArticolo);

                    // Converti Entity -> DTO per la response
                    ArticoloDto responseDto = articoliMapper.toDto(saved);

                    return ResponseEntity.ok(responseDto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
