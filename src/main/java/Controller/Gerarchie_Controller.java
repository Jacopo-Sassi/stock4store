package Controller;

import org.example.pw_be.api.GerarchieApi;
import org.example.pw_be.model.dto.Gerarchia;
import org.example.pw_be.model.dto.GerarchiaRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Gerarchie_Controller implements GerarchieApi {
    @Override
    public ResponseEntity<Gerarchia> _createGerarchia(GerarchiaRequest gerarchiaRequest) {
        return null;
    }

    @Override
    public ResponseEntity<List<Gerarchia>> _getGerarchie() {
        return null;
    }
}
