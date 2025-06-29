package com.th.eventmanagmentsystem.usermanagement.domain.exception;

public class InvalidEmailFormatException extends RuntimeException {

    public InvalidEmailFormatException(String msg) {
        super(msg);
    }

    public InvalidEmailFormatException() {
        super("Invalid Email Format");
    }
}
