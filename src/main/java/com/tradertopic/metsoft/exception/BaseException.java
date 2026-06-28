package com.tradertopic.metsoft.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

	private static final long serialVersionUID = 2517898139706757415L;
	
	private final HttpStatus status;
    private final String errorCode;     
    private final String messageKey;   
    private final Object[] args;       

    protected BaseException(HttpStatus status, String errorCode, String messageKey, Object... args) {
        super(messageKey);
        this.status = status;
        this.errorCode = errorCode;
        this.messageKey = messageKey;
        this.args = args;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}

