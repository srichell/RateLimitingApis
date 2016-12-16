package com.srichell.microservices.ratelimit.exceptions;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class DataReadException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataReadException() {

    }

    public DataReadException(String message) {
        super(message);
    }

    public DataReadException(Throwable cause) {
        super(cause);
    }

    public DataReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
