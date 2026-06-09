package com.stephen.Frame;

public class FrameNotFoundException extends RuntimeException {
    public FrameNotFoundException(Long id) {
        super("Frame not found: " + id);
    }
}