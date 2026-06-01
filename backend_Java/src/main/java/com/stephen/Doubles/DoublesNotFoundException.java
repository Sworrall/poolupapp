package com.stephen.Doubles;

public class DoublesNotFoundException extends RuntimeException {

    public DoublesNotFoundException(Long ID) {
        super("Doubles not found: " + ID);
    }
}
