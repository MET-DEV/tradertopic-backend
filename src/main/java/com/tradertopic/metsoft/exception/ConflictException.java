package com.tradertopic.metsoft.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {

	private static final long serialVersionUID = -7578061962595751667L;

	public ConflictException(String messageKey, Object... args) {
        super(HttpStatus.CONFLICT, "CONFLICT", messageKey, args);
    }
}
