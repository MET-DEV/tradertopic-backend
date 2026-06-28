package com.tradertopic.metsoft.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BaseException {

	private static final long serialVersionUID = -6369784436979301796L;

	public ResourceNotFoundException(String messageKey, Object... args) {
        super(HttpStatus.NOT_FOUND, "NOT_FOUND", messageKey, args);
    }
}
