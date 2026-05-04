package com.stephen.Frame.FrameFactory;

import com.stephen.Frame.Frame;
import com.stephen.BaseStats.StatHolder;

public interface FrameFactory<S extends StatHolder<S>> {
    Frame<S> createFrame(S party1, S party2);
    Frame<S> createFrame(S party1);
    Frame<S> createFrame();
}