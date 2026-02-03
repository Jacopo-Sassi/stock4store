package Controller;

import org.example.pw_be.api.ArticoliApi;
import org.example.pw_be.model.dto.Articolo;
import org.example.pw_be.model.dto.ArticoloRequest;
import org.example.pw_be.model.dto.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Articoli_Controller implements ArticoliApi {
    @Autowired
    private Service.Articolo_Service articoloService;


    @Override
    public ResponseEntity<Articolo> _createArticolo(ArticoloRequest articoloRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Void> _deleteArticolo(String codice) {
        return null;
    }

    @Override
    public ResponseEntity<List<Articolo>> _getArticoli(String gruppo, String stato, String stagione) {
        return null;
    }

    @Override
    public ResponseEntity<Articolo> _getArticoloByCodice(String codice) {
        return null;
    }

    @Override
    public ResponseEntity<Articolo> _getArticoloByEan(String ean) {
        return null;
    }

    @Override
    public ResponseEntity<List<Articolo>> _searchArticoli(SearchRequest searchRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Articolo> _updateArticolo(String codice, ArticoloRequest articoloRequest) {
        return null;
    }
}
