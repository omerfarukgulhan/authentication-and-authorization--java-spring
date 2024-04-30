package com.server.auth.token;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.auth.dto.Credentials;
import com.server.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

@Service
@ConditionalOnProperty(name = "auth-env.token-type", havingValue = "jwt")
public class JwtTokenService implements TokenService {
    private final SecretKey key = Keys.hmacShaKeyFor("qwerqwerqwerqwerqwerqwerqwerqwer".getBytes());

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Token createToken(User user, Credentials creds) {
        TokenSubject tokenSubject = new TokenSubject(user.getId(), user.isActive());
        try {
            String subject = mapper.writeValueAsString(tokenSubject);
            String token = Jwts.builder().setSubject(subject).signWith(key).compact();
            return new Token("Bearer", token);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User verifyToken(String authorizationHeader) {
        if (authorizationHeader == null) return null;
        var token = authorizationHeader.split(" ")[1];
        JwtParser parser = Jwts.parserBuilder().setSigningKey(key).build();
        try {
            Jws<Claims> claims = parser.parseClaimsJws(token);
            var subject = claims.getBody().getSubject();
            var tokenSubject = mapper.readValue(subject, TokenSubject.class);
            User user = new User();
            user.setId(tokenSubject.id());
            user.setActive(tokenSubject.active());
            return user;
        } catch (JwtException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static record TokenSubject(long id, boolean active) {
    }

}