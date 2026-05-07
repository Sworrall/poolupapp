package com.stephen.Frame;

import com.stephen.Player.Player;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.BaseStats_Service;
import com.stephen.BaseStats.StatField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

public class Frame_Singles extends Frame<Player>{
    private final BaseStats_Key frameKey;
    private static final Logger log = LoggerFactory.getLogger(Frame_Singles.class);


    // --- CONSTRUCTOR ---
    public Frame_Singles(Player p1, Player p2){
        super(p1, p2);
        this.frameKey = new BaseStats_Key(super.getID(), null);
        updateCloud_Frame();
    }

    public Frame_Singles(Player p1){
        super(p1, Player.createBye());
        this.frameKey = new BaseStats_Key(super.getID(), null);
        updateCloud_Frame();
    }

    public Frame_Singles(){
        super(Player.createBye(), Player.createBye());
        this.frameKey = new BaseStats_Key(super.getID(), null);
    }


    // --- FRAME OVERRIDE ---
    @Override
    public ArrayList<Player> getPlayersA() {
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(this.getParty1());
        return playerList;
    }

    @Override
    public ArrayList<Player> getPlayersB() {
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(this.getParty2());
        return playerList;
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
    }
}