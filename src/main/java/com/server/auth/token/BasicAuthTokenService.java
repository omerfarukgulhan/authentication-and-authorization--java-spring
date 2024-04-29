package com.server.auth.token;

import com.server.auth.dto.Credentials;
import com.server.user.User;
import com.server.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class BasicAuthTokenService implements TokenService {
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public BasicAuthTokenService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Token createToken(User user, Credentials credentials) {
        String emailColonPassword = credentials.email() + ":" + credentials.password();
        String token = Base64.getEncoder().encodeToString(emailColonPassword.getBytes());

        return new Token("Basic", token);
    }

    @Override
    public User verifyToken(String authorizationHeader) {
        if (authorizationHeader == null) return null;

        var base64Encoded = authorizationHeader.split("Basic ")[1];
        var decoded = new String(Base64.getDecoder().decode(base64Encoded));
        var credentials = decoded.split(":");
        var email = credentials[0];
        var password = credentials[1];

        User user = userService.findByEmail(email);

        if (user == null) return null;
        if (!passwordEncoder.matches(password, user.getPassword())) return null;

        return user;
    }
}
