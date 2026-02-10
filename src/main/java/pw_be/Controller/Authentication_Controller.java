package pw_be.Controller;


import org.example.pw_be.api.AuthenticationApi;
import org.example.pw_be.model.dto.LoginRequestDto;
import org.example.pw_be.model.dto.LoginResponseDto;
import org.springframework.http.ResponseEntity;

public class Authentication_Controller implements AuthenticationApi {
    @Override
    public ResponseEntity<LoginResponseDto> _login(LoginRequestDto loginRequest) {
        return null;
    }
}
