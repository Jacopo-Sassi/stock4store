package pw_be.Controller;

import org.example.pw_be.api.OrdiniApi;
import org.example.pw_be.model.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pw_be.Service.Ordine_Service;

import java.util.List;

@RestController
public class Ordini_Controller implements OrdiniApi {

    private final Ordine_Service ordineService;  // <-- Istanza!

    public Ordini_Controller(Ordine_Service ordineService) {
        this.ordineService = ordineService;
    }

    @Override
    public ResponseEntity<OrdineDto> _createOrdine(OrdineRequestDto ordineRequestDto) {
        OrdineDto createdOrdine = ordineService.createOrdine(ordineRequestDto);  // <-- istanza!
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrdine);
    }

    @Override
    public ResponseEntity<Void> _deleteOrdine(Long id) {
        ordineService.deleteOrdine(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<OrdineDto> _getOrdineById(Long id) {
        OrdineDto ordine = ordineService.getOrdineById(id);
        if (ordine == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ordine);
    }

    @Override
    public ResponseEntity<List<OrdineDto>> _getOrdini(StatoOrdineDto stato) {
        List<OrdineDto> ordini = ordineService.getOrdini(stato);
        return ResponseEntity.ok(ordini);
    }

    @Override
    public ResponseEntity<OrdineDto> _avanzaOrdine(Long id, AvanzaOrdineRequestDto avanzaOrdineRequestDto) {
        OrdineDto ordine = ordineService.avanzaOrdine(id, avanzaOrdineRequestDto.getNuovoStato());
        return ResponseEntity.ok(ordine);
    }
}
