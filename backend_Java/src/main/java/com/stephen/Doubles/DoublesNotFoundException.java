package com.stephen.Doubles;

public class DoublesNotFoundException extends RuntimeException {

    public DoublesNotFoundException(Long Id) {
        super("Doubles not found: " + Id);
    }
}
