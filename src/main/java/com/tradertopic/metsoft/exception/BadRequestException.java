package com.tradertopic.metsoft.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BaseException {

	private static final long serialVersionUID = -6540377453668412009L;

	public BadRequestException(String messageKey, Object... args) {
        super(HttpStatus.BAD_REQUEST, "BAD_REQUEST", messageKey, args);
    }
}