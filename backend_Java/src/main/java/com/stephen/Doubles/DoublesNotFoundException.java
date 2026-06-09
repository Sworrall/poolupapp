package com.stephen.Doubles;

public class DoublesNotFoundException extends RuntimeException {

    public DoublesNotFoundException(Long id) {
        super("Doubles not found: " + id);
    }
}
