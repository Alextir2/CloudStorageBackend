package alex.tir.storage.service;

import alex.tir.storage.dto.*;

public interface AuthenticationService {
    UserInfo signUp(UserForm userForm);

    JwtAuthenticationResponse singIn(LoginForm loginForm);

    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
