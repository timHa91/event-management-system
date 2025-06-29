package com.th.eventmanagmentsystem.usermanagement.domain.exception;

public class WeakPasswordException extends RuntimeException{

    public WeakPasswordException() {
        super("Das Passwort muss mindestens einen Großbuchstaben, einen Kleinbuchstaben, eine Zahl und ein Sonderzeichen enthalten.");
    }
}
