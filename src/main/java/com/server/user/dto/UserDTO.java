package com.server.user.dto;

import com.server.user.User;
import lombok.Data;

@Data
public class UserDTO {
    private long id;

    private String username;

    private String email;

    private String image;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.image = user.getImage();
    }
}
