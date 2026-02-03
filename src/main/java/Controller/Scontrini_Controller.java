package Controller;

import org.example.pw_be.api.ScontriniApi;
import org.example.pw_be.model.dto.ScontrinoDettaglio;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.List;

public class Scontrini_Controller implements ScontriniApi {
    @Override
    public ResponseEntity<List<ScontrinoDettaglio>> _getScontrini(OffsetDateTime dataInizio, OffsetDateTime dataFine) {
        return null;
    }
}
