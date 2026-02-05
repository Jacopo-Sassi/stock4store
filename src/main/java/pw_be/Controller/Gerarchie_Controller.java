package pw_be.Controller;

import pw_be.api.GerarchieApi;

import pw_be.model.dto.GerarchiaDto;
import pw_be.model.dto.GerarchiaRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Gerarchie_Controller implements GerarchieApi {
    @Override
    public ResponseEntity<List<GerarchiaDto>> _getGerarchie() {
        return null;
    }

    @Override
    public ResponseEntity<GerarchiaDto> _createGerarchia(GerarchiaRequestDto gerarchiaRequestDto) {
        return null;
    }
}
