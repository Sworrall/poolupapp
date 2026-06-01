package com.stephen.Frame;

public class FrameNotFoundException extends RuntimeException {
    public FrameNotFoundException(Long ID) {
        super("Frame not found: " + ID);
    }
}