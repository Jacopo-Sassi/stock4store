package Controller;

import org.example.pw_be.api.AuthenticationApi;
import org.example.pw_be.model.dto.LoginRequest;
import org.example.pw_be.model.dto.LoginResponse;
import org.springframework.http.ResponseEntity;

public class Authentication_Controller implements AuthenticationApi {
    @Override
    public ResponseEntity<LoginResponse> _login(LoginRequest loginRequest) {
        return null;
    }
}
