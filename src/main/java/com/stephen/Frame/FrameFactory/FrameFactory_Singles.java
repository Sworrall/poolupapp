package com.stephen.Frame.FrameFactory;

import com.stephen.Frame.Frame;
import com.stephen.Frame.Frame_Singles;
import com.stephen.Player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameFactory_Singles implements FrameFactory<Player> {
    private static final Logger log = LoggerFactory.getLogger(FrameFactory_Singles.class);

    @Override
    public Frame<Player> createFrame(Player p1, Player p2) {
        log.info("Creating singles frame: {} vs {}", p1.getName(), p2.getName());
        return new Frame_Singles(p1, p2);
    }

    @Override
    public Frame<Player> createFrame(Player p1) {
        log.info("Creating singles frame with bye: {}", p1.getName());
        return new Frame_Singles(p1);
    }

    @Override
    public Frame<Player> createFrame() {
        log.info("Creating empty singles frame");
        return new Frame_Singles();
    }
}