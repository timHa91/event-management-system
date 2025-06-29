package com.th.eventmanagmentsystem.usermanagement.domain.exception;

public class EmailAlreadyExistsException extends RuntimeException{

    public EmailAlreadyExistsException() {
        super("Email already exists");
    }

    public EmailAlreadyExistsException(String msg) {
        super(msg);
    }
}
