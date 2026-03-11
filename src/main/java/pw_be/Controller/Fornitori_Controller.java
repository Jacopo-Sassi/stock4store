package pw_be.Controller;


import org.example.pw_be.api.FornitoriApi;
import org.example.pw_be.model.dto.FornitoreDto;
import org.example.pw_be.model.dto.FornitoreRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pw_be.Service.Fornitore_Service;

import java.util.List;

@RestController
public class Fornitori_Controller implements FornitoriApi {

    private final Fornitore_Service fornitoreService;

    // Constructor injection (best practice)
    public Fornitori_Controller(Fornitore_Service fornitoreService) {
        this.fornitoreService = fornitoreService;
    }

    @Override
    public ResponseEntity<FornitoreDto> _createFornitore(FornitoreRequestDto fornitoreRequestDto) {
        FornitoreDto createdFornitore = fornitoreService.createFornitore(fornitoreRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFornitore);
    }

    @Override
    public ResponseEntity<FornitoreDto> _getFornitoreById(Long id) {
        FornitoreDto fornitore = fornitoreService.getFornitoreById(id);
        return ResponseEntity.ok(fornitore);
    }

    @Override
    public ResponseEntity<List<FornitoreDto>> _getFornitori() {
        List<FornitoreDto> fornitori = fornitoreService.getAllFornitori();
        return ResponseEntity.ok(fornitori);
    }

    @Override
    public ResponseEntity<List<FornitoreDto>> _searchFornitori(String query) {
        List<FornitoreDto> fornitori = fornitoreService.searchFornitori(query);
        return ResponseEntity.ok(fornitori);
    }

    @Override
    public ResponseEntity<FornitoreDto> _updateFornitore(Long id, FornitoreRequestDto fornitoreRequestDto) {
        FornitoreDto updatedFornitore = fornitoreService.updateFornitore(id, fornitoreRequestDto);
        return ResponseEntity.ok(updatedFornitore);
    }

    @Override
    public ResponseEntity<Void> _deleteFornitore(Long id) {
        fornitoreService.deleteFornitore(id);
        return ResponseEntity.noContent().build();
    }
}
