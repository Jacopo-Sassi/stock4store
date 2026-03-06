package pw_be.Service;

import org.example.pw_be.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pw_be.Model.Utente;
import pw_be.Repository.Utente_Repository;
import pw_be.Util.JwtUtil;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class Authentication_Service {

    @Autowired
    private Utente_Repository utenteRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponseDto login(LoginRequestDto loginRequest) {
        Utente utente = utenteRepository.findByNomeUtente(loginRequest.getNomeUtente())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenziali non valide"
                ));

        if (!utente.getPassword().equals(loginRequest.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Credenziali non valide"
            );
        }

        // Verifica se utente è attivo
        if (!utente.getAttivo()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Utente disabilitato"
            );
        }

        // Aggiorna ultimo accesso
        utente.setUltimoAccesso(LocalDateTime.now());
        utenteRepository.save(utente);

        // Genera token JWT
        String token = jwtUtil.generateToken(utente.getNomeUtente(), utente.getRuolo());

        // Crea risposta
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setUtente(mapToUtenteResponse(utente));

        return response;
    }

    public UtenteResponseDto getCurrentUser(String token) {
        String nomeUtente = jwtUtil.getNomeUtenteFromToken(token);

        Utente utente = utenteRepository.findByNomeUtente(nomeUtente)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utente non trovato"
                ));

        return mapToUtenteResponse(utente);
    }

    public VerifyToken200ResponseDto verifyToken(String token) {
        VerifyToken200ResponseDto response = new VerifyToken200ResponseDto();

        if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
            String nomeUtente = jwtUtil.getNomeUtenteFromToken(token);
            Utente utente = utenteRepository.findByNomeUtente(nomeUtente)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Token non valido"
                    ));

            response.setValid(true);
            response.setUtente(mapToUtenteResponse(utente));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token non valido o scaduto"
            );
        }

        return response;
    }

    public ChangePassword200ResponseDto changePassword(String token, ChangePasswordRequestDto request) {
        String nomeUtente = jwtUtil.getNomeUtenteFromToken(token);

        Utente utente = utenteRepository.findByNomeUtente(nomeUtente)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utente non trovato"
                ));

        // Verifica password attuale
        if (!utente.getPassword().equals(request.getPasswordAttuale())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password attuale non corretta"
            );
        }

        // Aggiorna password
        utente.setPassword(request.getNuovaPassword());
        utenteRepository.save(utente);

        ChangePassword200ResponseDto response = new ChangePassword200ResponseDto();
        response.setMessage("Password modificata con successo");
        return response;
    }

    public Logout200ResponseDto logout() {
        Logout200ResponseDto response = new Logout200ResponseDto();
        response.setMessage("Logout effettuato con successo");
        return response;
    }

    private UtenteResponseDto mapToUtenteResponse(Utente utente) {
        UtenteResponseDto dto = new UtenteResponseDto();
        dto.setId(utente.getId());
        dto.setNomeUtente(utente.getNomeUtente());
        dto.setEmail(utente.getEmail());
        dto.setRuolo(UtenteResponseDto.RuoloEnum.fromValue(utente.getRuolo()));
        dto.setAttivo(utente.getAttivo());

        if (utente.getDataCreazione() != null) {
            dto.setDataCreazione(OffsetDateTime.of(utente.getDataCreazione(), ZoneOffset.UTC));
        }

        if (utente.getUltimoAccesso() != null) {
            dto.setUltimoAccesso(OffsetDateTime.of(utente.getUltimoAccesso(), ZoneOffset.UTC));
        }

        return dto;
    }
}
