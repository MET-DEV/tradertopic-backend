package com.tradertopic.metsoft.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseException {

	private static final long serialVersionUID = -8011588294812018473L;

	public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", "error.forbidden");
    }

    public ForbiddenException(String messageKey, Object... args) {
        super(HttpStatus.FORBIDDEN, "FORBIDDEN", messageKey, args);
    }
}