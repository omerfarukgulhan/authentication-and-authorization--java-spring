package com.server.user.exception;

import java.util.Collections;
import java.util.Map;

import com.server.utils.Messages;
import org.springframework.context.i18n.LocaleContextHolder;

public class NotUniqueEmailException extends RuntimeException {

    public NotUniqueEmailException() {
        super(Messages.getMessageForLocale("auth.error.validation", LocaleContextHolder.getLocale()));
    }

    public Map<String, String> getValidationErrors() {
        return Collections.singletonMap("email", Messages.getMessageForLocale("auth.constraint.email.notunique", LocaleContextHolder.getLocale()));
    }

}