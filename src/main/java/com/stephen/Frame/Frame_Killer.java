package com.stephen.Frame;

import com.stephen.Functions.UserInput;
import com.stephen.Player.Player;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.BaseStats_Service;
import com.stephen.BaseStats.StatField;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Frame_Killer <S extends StatHolder<S>> extends Frame<Player> {
    private final HashMap<S, Integer> partyLives;
    private final BaseStats_Key frameKey;
    private static final Logger log = LoggerFactory.getLogger(Frame_Killer.class);


    // --- CONSTRUCTOR ---
    public Frame_Killer(ArrayList<S> allParty, int lives){
        super(new Player(), new Player());
        partyLives = new HashMap<>();
        for (S s : allParty) { partyLives.put(s, lives); }
        this.frameKey = new BaseStats_Key(this.getID(), null);
        log.info("Frame_Killer created with {} players and {} lives each.", allParty.size(), lives);
    }


    // --- GETTERS ---
    @Override
    public ArrayList<Player> getPlayersA() {
        ArrayList<Player> playerList = new ArrayList<>();
        for (S s : this.partyLives.keySet()) {
            playerList.add((Player) s);
        }
        log.info("getPlayersA called, returning {} players.", playerList.size());
        return playerList;
    }

    @Override
    public ArrayList<Player> getPlayersB() {
        log.info("getPlayersB called, returning empty list.");
        return new ArrayList<>();
    }


    // --- LOGIC ---
    public void PlayFrame() {
        while (partyLives.size() > 1) {
            ArrayList<S> players = new ArrayList<>(partyLives.keySet());
            Collections.shuffle(players);
            for (S player : players) {
                boolean shot = UserInput.shotResult_KILLER();
                boolean shotBlack = UserInput.shotBlackBall_KILLER();
                if (!shot) {
                    int lives = partyLives.get(player) - 1;
                    if (lives <= 0) {
                        partyLives.remove(player);
                    } else {
                        partyLives.put(player, lives);
                    }
                } else if (shotBlack) {
                    partyLives.put(player, partyLives.get(player) + 1);
                }
            }
        }
        log.info("PlayFrame completed, winner: {}", partyLives.keySet().iterator().next());
    }

    @Override
    public void PlayOutFrame() {
        PlayFrame();
        log.info("PlayOutFrame completed.");
    }

    @Override
    public void recordFrame() {
        for (int i = 0; i < this.partyLives.size(); i++) {
            S p1 = partyLives.keySet().iterator().next();
            BaseStats_Service.applyEvent(frameKey, StatField.FRAME_TOTAL, p1);
            BaseStats_Service.applyFrame_WIN_LOSS(frameKey, frameKey, this);
        }
        updateCloud_Frame();
        log.info("recordFrame completed, stats updated for {} players.", partyLives.size());
    }
}
