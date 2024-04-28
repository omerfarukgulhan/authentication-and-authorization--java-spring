package com.server.user.exception;

import com.server.utils.Messages;
import org.springframework.context.i18n.LocaleContextHolder;

public class ActivationNotificationException extends RuntimeException {
    public ActivationNotificationException() {
        super(Messages.getMessageForLocale("auth.create.user.email.failure", LocaleContextHolder.getLocale()));
    }
}
