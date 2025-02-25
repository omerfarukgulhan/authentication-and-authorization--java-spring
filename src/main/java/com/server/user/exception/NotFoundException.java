package com.server.user.exception;

import com.server.utils.Messages;
import org.springframework.context.i18n.LocaleContextHolder;

public class NotFoundException extends RuntimeException {
    public NotFoundException(long id) {
        super(Messages.getMessageForLocale("auth.user.not.found", LocaleContextHolder.getLocale(), id));
    }
}