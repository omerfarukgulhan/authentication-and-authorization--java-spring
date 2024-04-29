package com.server.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private boolean active = false;

    @JsonIgnore
    private String activationToken;

    @Lob
    private String image;
}
