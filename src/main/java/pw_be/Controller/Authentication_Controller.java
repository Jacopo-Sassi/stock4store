package pw_be.Controller;

import pw_be.api.AuthenticationApi;
import pw_be.model.dto.LoginRequestDto;
import pw_be.model.dto.LoginResponseDto;
import org.springframework.http.ResponseEntity;

public class Authentication_Controller implements AuthenticationApi {
    @Override
    public ResponseEntity<LoginResponseDto> _login(LoginRequestDto loginRequest) {
        return null;
    }
}
