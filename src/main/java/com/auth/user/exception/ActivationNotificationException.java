package com.auth.user.exception;

import com.auth.utils.Messages;
import org.springframework.context.i18n.LocaleContextHolder;

public class ActivationNotificationException extends RuntimeException {
    public ActivationNotificationException() {
        super(Messages.getMessageForLocale("auth.create.user.email.failure", LocaleContextHolder.getLocale()));
    }
}
