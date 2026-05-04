package com.stephen.Frame.FrameFactory;

import java.util.ArrayList;

import com.stephen.Frame.Frame_Killer;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameFactory_Killer<S extends StatHolder<S>> {
    private static final Logger log = LoggerFactory.getLogger(FrameFactory_Killer.class);

    public Frame_Killer<S> createKillerFrame(ArrayList<S> parties, int lives) {
        log.info("Creating Killer frame with {} players and {} lives", parties.size(), lives);
        return new Frame_Killer<>(parties, lives);
    }
}