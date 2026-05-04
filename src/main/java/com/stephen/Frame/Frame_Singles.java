package com.stephen.Frame;

import com.stephen.Player.Player;
import com.stephen.Stats.BaseStats_Key;
import com.stephen.Stats.BaseStats_Service;
import com.stephen.Stats.StatField;
import com.stephen.Stats.StatHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

public class Frame_Singles <S extends StatHolder<S>> extends Frame<Player>{
    private final BaseStats_Key frameKey;
    private static final Logger log = LoggerFactory.getLogger(Frame_Singles.class);


    // --- CONSTRUCTOR ---
    public Frame_Singles(Player p1, Player p2){
        super(p1, p2);
        this.frameKey = new BaseStats_Key(super.getID(), null);
        log.info("Created new Frame_Singles with ID: {} and players: {} vs {}", super.getID(), p1.getName(), p2.getName());
    }

    public Frame_Singles(Player p1){
        super(p1, new Player());
        this.frameKey = new BaseStats_Key(super.getID(), null);
        log.info("Created new Frame_Singles with ID: {} and player: {} vs BYE", super.getID(), p1.getName());
    }

    public Frame_Singles(){
        super(new Player(), new Player());
        this.frameKey = new BaseStats_Key(super.getID(), null);
        log.info("Created new Frame_Singles with ID: {} and players: BYE vs BYE", super.getID());
    }


    // --- GETTERS ---
    @Override
    public ArrayList<Player> getPlayersA() {
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(this.getParty1());
        log.info("getPlayersA called for Frame ID: {} returning player: {}", super.getID(), this.getParty1().getName());
        return playerList;
    }

    @Override
    public ArrayList<Player> getPlayersB() {
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(this.getParty2());
        log.info("getPlayersB called for Frame ID: {} returning player: {}", super.getID(), this.getParty2().getName());
        return playerList;
    }


    // --- INTERFACE ---
    @Override
    public void PlayOutFrame() {
        this.handleBye(super.getParty1(), super.getParty2());
        this.playFrame();
        log.info("PlayOutFrame called for Frame ID: {} with players: {} vs {}", super.getID(), super.getParty1().getName(), super.getParty2().getName());
    }

    @Override
    public void recordFrame() {
        recordSingles_Frame();
        log.info("recordFrame called for Frame ID: {} with players: {} vs {}", super.getID(), super.getParty1().getName(), super.getParty2().getName());
    }


    // --- STATS LOGIC ---
    public void recordSingles_Frame(){
        Player playerA = this.getParty1();
        Player playerB = this.getParty2();
        BaseStats_Service.applyEvent(frameKey, StatField.FRAME_TOTAL, playerA);
        BaseStats_Service.applyEvent(frameKey, StatField.FRAME_TOTAL, playerB);
        BaseStats_Service.applyFrame_WIN_LOSS(frameKey, frameKey, this);
        log.info("recordSingles_Frame called for Frame ID: {} with players: {} vs {}", super.getID(), playerA.getName(), playerB.getName());
    }
}