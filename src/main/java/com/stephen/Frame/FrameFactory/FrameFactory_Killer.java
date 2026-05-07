package com.stephen.Frame.FrameFactory;

import java.util.ArrayList;

import com.stephen.Frame.Frame_Killer;
import com.stephen.Player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameFactory_Killer {
    private static final Logger log = LoggerFactory.getLogger(FrameFactory_Killer.class);

    public Frame_Killer createKillerFrame(ArrayList<Player> parties, int lives) {
        log.info("Creating Killer frame with {} players and {} lives", parties.size(), lives);
        return new Frame_Killer(parties, lives);
    }
}