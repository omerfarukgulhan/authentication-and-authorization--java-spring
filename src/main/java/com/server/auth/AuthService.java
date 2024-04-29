package com.server.auth;

import com.server.auth.dto.AuthResponse;
import com.server.auth.dto.Credentials;
import com.server.auth.exception.AuthenticationException;
import com.server.auth.token.Token;
import com.server.auth.token.TokenService;
import com.server.user.User;
import com.server.user.UserService;
import com.server.user.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;

    private final TokenService tokenService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserService userService, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse authenticate(Credentials credentials) {
        User user = userService.findByEmail(credentials.email());

        if (user == null) throw new AuthenticationException();
        if (!passwordEncoder.matches(credentials.password(), user.getPassword())) throw new AuthenticationException();

        Token token = tokenService.createToken(user, credentials);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setUser(new UserDTO(user));

        return authResponse;
    }
}
