package com.elk.spike.elkspike.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class HandledServiceException extends RuntimeException{

    private static final long serialVersionUID = 2509102951291732259L;

    public HandledServiceException() {
        super();
    }

    public HandledServiceException(String message) {
        super(message);
    }

    public HandledServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public HandledServiceException(Throwable cause) {
        super(cause);
    }
}
