package pw_be.Controller;

import pw_be.api.ScontriniApi;
import pw_be.model.dto.ScontrinoDettaglioDto;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;

public class Scontrini_Controller implements ScontriniApi {

    @Override
    public ResponseEntity<List<ScontrinoDettaglioDto>> _getScontrini(OffsetDateTime dataInizio, OffsetDateTime dataFine) {
        return null;
    }
}
