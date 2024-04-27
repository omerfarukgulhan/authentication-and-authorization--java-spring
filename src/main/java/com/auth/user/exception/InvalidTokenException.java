package com.auth.user.exception;

import com.auth.utils.Messages;
import org.springframework.context.i18n.LocaleContextHolder;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException() {
        super(Messages.getMessageForLocale("auth.activate.user.invalid.token", LocaleContextHolder.getLocale()));
    }
}