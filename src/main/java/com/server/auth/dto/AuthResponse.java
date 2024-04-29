package com.server.auth.dto;

import com.server.auth.token.Token;
import com.server.user.dto.UserDTO;
import lombok.Data;

@Data
public class AuthResponse {
    UserDTO user;
    
    Token token;
}
