package com.stephen.Match;

import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.BaseStats_Service;
import com.stephen.Frame.Frame;
import com.stephen.Frame.FrameFactory.FrameFactory;
import com.stephen.Player.Player;
import com.stephen.BaseStats.StatField;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class Match_Singles extends Match<Player>{
    public BaseStats_Key matchKey;
    private final FrameFactory<Player> frameFactory;
    private static final Logger log = LoggerFactory.getLogger(Match_Singles.class);


    // --- CONSTRUCTOR ---
    public Match_Singles(Player p1, Player p2, int frameCount, FrameFactory<Player> frameFactory) {
        super(p1, p2, frameCount, frameFactory);
        this.matchKey = new BaseStats_Key(super.getID(), null);
        this.frameFactory = frameFactory;
        super.isPlayed = false;
        super.isBye = false;
        super.isDraw = false;
        log.info("Created Match_Singles: {} with {} frames.", super.errorCapture(), super.getFrameCount());
    }

    public Match_Singles(Player p, int frameCount, FrameFactory<Player> frameFactory){
        super(p, frameCount, frameFactory);
        this.matchKey = new BaseStats_Key(super.getID(), null);
        this.frameFactory = frameFactory;
        super.isPlayed = false;
        super.isBye = true;
        super.isDraw = false;
        log.info("Created Match_Singles: {} with {} frames.", super.errorCapture(), super.getFrameCount());
    }

    public Match_Singles(FrameFactory<Player> frameFactory){
        super(Player.createBye(), Player.createBye(), 0, frameFactory);
        this.matchKey = new BaseStats_Key(super.getID(), null);
        this.frameFactory = frameFactory;
        this.isPlayed = true;
        this.isBye = true;
        this.isDraw = false;
        log.info("Created Match_Singles: {} with {} frames.", super.errorCapture(), super.getFrameCount());
    }


    // --- INTERFACE ---
    @Override
    public void playMatch(){
        handleByeMatch();
        if(!isBye){
            for (int i = 0; i < this.getFrameCount(); i++) {
                Frame<Player> f = super.frameFactory.createFrame(party1, party2);
                super.frames.add(f);
                f.playFrame();
            }
            isPlayed = true;
            long party1Wins = frames.stream()
                    .filter(f -> f.getWinner().equals(party1))
                    .count();
            long party2Wins = frames.stream()
                    .filter(f -> f.getWinner().equals(party2))
                    .count();
            if (party1Wins > party2Wins) {
                winner = party1;
                loser = party2;
            } else if (party2Wins > party1Wins) {
                winner = party2;
                loser = party1;
            } else {
                isDraw = true;
                winner = null;
                loser = null;
            }
        }
        recordPlayer_Match(this);
        log.info("Played Match_Singles: {} vs {}. Result: {}", party1.getName(), party2.getName(), isDraw ? "Draw" : (getWinner().getName() + " wins"));
    }

    @Override
    public Player createByeParty() {
        log.info("Creating bye party for Match_Singles");
        return new Player();
    }


    // --- GETTERS ---
    public int getTotalFrames(){
        log.info("Getting total frames for Match_Singles: {}", getFrameCount());
        return super.getFrameCount();
    }


    // --- LOGIC ---
    public void recordPlayer_Match(Match<Player> m) {
        Player p1 = m.getParty1();
        Player p2 = m.getParty2();
        BaseStats_Service.applyEvent(matchKey, StatField.MATCH_TOTAL, p1);
        BaseStats_Service.applyEvent(matchKey, StatField.MATCH_TOTAL, p2);
        if(this.isPlayed()){
            if (super.isDraw()) {
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_DRAW, p1);
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_DRAW, p2);
            } else if (this.getWinner().equals(super.getParty1())) {
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_WIN, p1);
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_LOSS, p2);
            } else {
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_LOSS, p1);
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_WIN, p2);
            }
        }
        log.info("Recorded player match stats for Match_Singles: {} vs {}", p1.getName(), p2.getName());
    }

    public void recordMatch() {
        recordPlayer_Match(this);
        updateCloud_Match();
    }
}
