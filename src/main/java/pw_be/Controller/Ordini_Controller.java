package pw_be.Controller;


import org.example.pw_be.api.OrdiniApi;
import org.example.pw_be.model.dto.AvanzaOrdineRequestDto;
import org.example.pw_be.model.dto.OrdineDto;
import org.example.pw_be.model.dto.OrdineRequestDto;
import org.example.pw_be.model.dto.StatoOrdineDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Ordini_Controller implements OrdiniApi {
    @Override
    public ResponseEntity<OrdineDto> _avanzaOrdine(Long id, AvanzaOrdineRequestDto avanzaOrdineRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<OrdineDto> _createOrdine(OrdineRequestDto ordineRequestDto) {
        return null;
    }

    @Override
    public ResponseEntity<OrdineDto> _getOrdineById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<OrdineDto>> _getOrdini(StatoOrdineDto stato) {
        return null;
    }
}
