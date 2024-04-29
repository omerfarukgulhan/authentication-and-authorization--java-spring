package com.server.auth.token;

import com.server.auth.dto.Credentials;
import com.server.user.User;

public interface TokenService {
    public Token createToken(User user, Credentials credentials);

    public User verifyToken(String authorizationHeader);
}
