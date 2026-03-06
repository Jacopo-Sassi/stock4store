package pw_be.Controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.pw_be.api.AuthenticationApi;
import org.example.pw_be.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import pw_be.Service.Authentication_Service;

@RestController
public class Authentication_Controller implements AuthenticationApi {

    @Autowired
    private Authentication_Service authenticationService;

    @Autowired
    private HttpServletRequest request;

    @Override
    public ResponseEntity<LoginResponseDto> _login(LoginRequestDto loginRequest) {
        LoginResponseDto response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<VerifyToken200ResponseDto> _verifyToken() {
        String token = extractTokenFromRequest();
        VerifyToken200ResponseDto response = authenticationService.verifyToken(token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Logout200ResponseDto> _logout() {
        Logout200ResponseDto response = authenticationService.logout();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UtenteResponseDto> _getCurrentUser() {
        String token = extractTokenFromRequest();
        UtenteResponseDto response = authenticationService.getCurrentUser(token);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ChangePassword200ResponseDto> _changePassword(ChangePasswordRequestDto changePasswordRequestDto) {
        String token = extractTokenFromRequest();
        ChangePassword200ResponseDto response = authenticationService.changePassword(token, changePasswordRequestDto);
        return ResponseEntity.ok(response);
    }

    private String extractTokenFromRequest() {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token di autenticazione mancante"
            );
        }

        return authHeader.substring(7); // Rimuove "Bearer "
    }
}
