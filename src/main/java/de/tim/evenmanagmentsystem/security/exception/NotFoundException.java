package de.tim.evenmanagmentsystem.security.exception;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String entityName) {
        super(entityName + " not found");
    }
}
