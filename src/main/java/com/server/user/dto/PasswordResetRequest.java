package com.server.user.dto;

import jakarta.validation.constraints.Email;

public record PasswordResetRequest(@Email String email) {

}
