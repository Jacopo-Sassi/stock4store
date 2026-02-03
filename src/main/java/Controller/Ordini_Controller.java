package Controller;

import org.example.pw_be.api.OrdiniApi;
import org.example.pw_be.model.dto.AvanzaOrdineRequest;
import org.example.pw_be.model.dto.Ordine;
import org.example.pw_be.model.dto.OrdineRequest;
import org.example.pw_be.model.dto.StatoOrdine;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class Ordini_Controller implements OrdiniApi {
    @Override
    public ResponseEntity<Ordine> _avanzaOrdine(Long id, AvanzaOrdineRequest avanzaOrdineRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Ordine> _createOrdine(OrdineRequest ordineRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Ordine> _getOrdineById(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<Ordine>> _getOrdini(StatoOrdine stato) {
        return null;
    }
}
