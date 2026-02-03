package Controller;

import org.example.pw_be.api.FornitoriApi;
import org.example.pw_be.model.dto.Fornitore;
import org.example.pw_be.model.dto.FornitoreRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Fornitori_Controller implements FornitoriApi {
    @Override
    public ResponseEntity<Fornitore> _createFornitore(FornitoreRequest fornitoreRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Fornitore> _getFornitoreById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<Fornitore>> _getFornitori() {
        return null;
    }

    @Override
    public ResponseEntity<List<Fornitore>> _searchFornitori(String query) {
        return null;
    }

    @Override
    public ResponseEntity<Fornitore> _updateFornitore(Long id, FornitoreRequest fornitoreRequest) {
        return null;
    }
}
