package ru.nikidzawa.app.responses.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException (String message) {
        super(message);
    }
}
