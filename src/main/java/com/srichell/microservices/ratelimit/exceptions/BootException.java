package com.srichell.microservices.ratelimit.exceptions;

/**
 * Created by Sridhar Chellappa on 12/16/16.
 */
public class BootException extends Exception {
    public BootException() {

    }

    public BootException(String message) {
        super(message);
    }

    public BootException(Throwable cause) {
        super(cause);
    }

    public BootException(String message, Throwable cause) {
        super(message, cause);
    }
}
