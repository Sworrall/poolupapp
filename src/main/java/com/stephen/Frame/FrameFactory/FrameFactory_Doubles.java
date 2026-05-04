package com.stephen.Frame.FrameFactory;

import com.stephen.Doubles.Doubles;
import com.stephen.Frame.Frame;
import com.stephen.Frame.Frame_Doubles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameFactory_Doubles implements FrameFactory<Doubles> {
    private static final Logger log = LoggerFactory.getLogger(FrameFactory_Doubles.class);

    @Override
    public Frame<Doubles> createFrame(Doubles d1, Doubles d2) {
        log.info("Creating doubles frame: {} vs {}", d1.getName(), d2.getName());
        return new Frame_Doubles<>(d1, d2);
    }

    @Override
    public Frame<Doubles> createFrame(Doubles d1) {
        log.info("Creating doubles frame with bye: {}", d1.getName());
        return new Frame_Doubles<>(d1);
    }

    @Override
    public Frame<Doubles> createFrame() {
        log.info("Creating empty doubles frame");
        return new Frame_Doubles<>();
    }
}