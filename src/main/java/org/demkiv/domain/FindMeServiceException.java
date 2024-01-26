package org.demkiv.domain;

public class FindMeServiceException extends RuntimeException {
    public FindMeServiceException(String message) {
        super(message);
    }

    public FindMeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
