package com.server.user.dto;

import com.server.user.validation.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdate(
        @NotBlank(message = "{auth.constraint.username.notblank}")
        @Size(min = 4, max = 255)
        String username,
        @FileType(types = {"jpeg", "png"})
        String image
) {
}