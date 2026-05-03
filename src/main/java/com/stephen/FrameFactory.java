package com.stephen;

import java.util.ArrayList;

public interface FrameFactory<S extends StatHolder<S>> {
    Frame<S> createFrame(S party1, S party2);
    Frame<S> createFrame(S party1);
    Frame<S> createFrame();
}