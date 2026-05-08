package com.stephen.Frame;

import com.stephen.Functions.UserInput;
import com.stephen.Player.Player;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.BaseStats_Service;
import com.stephen.BaseStats.StatField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Frame_Killer extends Frame<Player> {
    private final HashMap<Player, Integer> partyLives;
    ArrayList<Player> players;
    private final BaseStats_Key frameKey;
    private static final Logger log = LoggerFactory.getLogger(Frame_Killer.class);


    // --- CONSTRUCTOR ---
    public Frame_Killer(ArrayList<Player> allParty, int lives){
        super(new Player(), new Player());
        this.partyLives = new HashMap<>();
        this.players = allParty;
        for (Player p : allParty) {
            partyLives.put(p, lives);
        }
        this.frameKey = new BaseStats_Key(this.getID(), null);
    }


    // --- FRAME OVERRIDE ---
    @Override
    public void playOutFrame() {
        playFrame();
        recordFrame();
        updateCloud_Frame();
        log.info("Killer frame Played: {}", this.getID());

    }

    @Override
    public void recordFrame() {
        Player winner = partyLives.keySet().iterator().next();
        for (Player p : players) {
            BaseStats_Service.applyEvent(frameKey, StatField.FRAME_TOTAL, p);
            if (p.equals(winner)) {
                BaseStats_Service.applyEvent(frameKey, StatField.FRAME_WIN, p);
            }
        }
    }

    @Override
    public ArrayList<Player> getPlayersA() {
        return new ArrayList<>(this.partyLives.keySet());
    }

    @Override
    public ArrayList<Player> getPlayersB() {
        log.warn("getPlayersB called, returning empty list.");
        return new ArrayList<>();
    }


    // --- LOGIC ---
    public void playFrame() {
        handleBye();
        while (partyLives.size() > 1) {
            playRack();
        }
        log.info("PlayFrame completed, winner: {}", partyLives.keySet().iterator().next().getName());
    }

    private void playRack() {
        ArrayList<Player> players = new ArrayList<>(partyLives.keySet());
        Collections.shuffle(players);

        for (Player p : players) {
            if (!partyLives.containsKey(p)) continue;
            boolean potted = UserInput.shotResult_KILLER();
            boolean foul   = UserInput.foulResult_KILLER();
            boolean black  = UserInput.shotBlackBall_KILLER();
            if (black) {
                partyLives.put(p, partyLives.get(p) + 1);
            } else if (foul || !potted) {
                int lives = partyLives.get(p) - 1;
                if (lives <= 0) {
                    partyLives.remove(p);
                    BaseStats_Service.applyEvent(frameKey, StatField.FRAME_LOSS, p);
                } else {
                    partyLives.put(p, lives);
                }
            }
        }
    }
}
