package com.stephen.Frame;

public class FrameNotFoundException extends RuntimeException {
    public FrameNotFoundException(Long Id) {
        super("Frame not found: " + Id);
    }
}