package com.server.auth.exception;

import com.server.utils.Messages;
import org.springframework.context.i18n.LocaleContextHolder;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super(Messages.getMessageForLocale("auth.auth.invalid.credentials", LocaleContextHolder.getLocale()));
    }
}
