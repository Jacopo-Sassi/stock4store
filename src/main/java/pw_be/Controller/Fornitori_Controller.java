package pw_be.Controller;

import pw_be.api.FornitoriApi;
import pw_be.model.dto.FornitoreDto;
import pw_be.model.dto.FornitoreRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Fornitori_Controller implements FornitoriApi {
    @Override
    public ResponseEntity<FornitoreDto> _createFornitore(FornitoreRequestDto fornitoreRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<FornitoreDto> _getFornitoreById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<FornitoreDto>> _getFornitori() {
        return null;
    }

    @Override
    public ResponseEntity<List<FornitoreDto>> _searchFornitori(String query) {
        return null;
    }

    @Override
    public ResponseEntity<FornitoreDto> _updateFornitore(Long id, FornitoreRequestDto fornitoreRequestDto) {
        return null;
    }
}
