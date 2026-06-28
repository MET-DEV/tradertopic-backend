package com.tradertopic.metsoft.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BaseException {

	private static final long serialVersionUID = 1604082073563853722L;

	public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "error.unauthorized");
    }

    public UnauthorizedException(String messageKey, Object... args) {
        super(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", messageKey, args);
    }
}
