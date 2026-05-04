package com.stephen.Match;

import com.stephen.Doubles.Doubles;
import com.stephen.Frame.Frame;
import com.stephen.FrameFactory.FrameFactory;
import com.stephen.Player.Player;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.BaseStats_Service;
import com.stephen.BaseStats.StatField;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Match_Doubles extends Match<Doubles>{
    private final BaseStats_Key matchKeyA;
    private final BaseStats_Key matchKeyB;
    private static final Logger log = LoggerFactory.getLogger(Match_Doubles.class);


    // --- CONSTRUCTOR ---
    public Match_Doubles(Doubles teamA, Doubles teamB, int frameCount, FrameFactory<Doubles> frameFactory){
        super(teamA, teamB, frameCount, frameFactory);
        this.matchKeyA = new BaseStats_Key(super.getID(), teamA.getID());
        this.matchKeyB = new BaseStats_Key(super.getID(), teamB.getID());
        this.isPlayed = false;
        this.isBye = false;
        this.isDraw = false;
        log.info("Created Match_Doubles: {} with {} frames.", super.errorCapture(), frameCount);
    }

    public Match_Doubles(Doubles team, int frameCount, FrameFactory<Doubles> frameFactory) {
        super(team, frameCount, frameFactory);
        this.matchKeyA = new BaseStats_Key(super.getID(), team.getID());
        this.matchKeyB = new BaseStats_Key(super.getID(), 0);
        this.isPlayed = false;
        this.isBye = true;
        this.isDraw = false;
        log.info("Created Match_Doubles with bye: {} with {} frames.", this.errorCapture(), super.getFrameCount());
    }

    public Match_Doubles(FrameFactory<Doubles> frameFactory) {
        super(Doubles.createBye(), Doubles.createBye(),0,  frameFactory);
        this.matchKeyA = new BaseStats_Key(super.getID(), 0);
        this.matchKeyB = new BaseStats_Key(super.getID(), 0);
        this.isPlayed = false;
        this.isBye = true;
        this.isDraw = false;
        log.info("Created Match_Doubles with bye: {} with {} frames.", super.errorCapture(), super.getFrameCount());
    }


    // --- INTERFACE ---
    @Override
    public void playMatch(){
        handleByeMatch();
        if(!isBye){
            for (int i = 0; i < this.getFrameCount(); i++) {
                Frame<Doubles> f = frameFactory.createFrame(party1, party2);
                frames.add(f);
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
        recordDoublesTeam_Match();
        recordDoublesPlayer_Match();
        log.info("Played Match_Doubles: {}. Result: {}", super.errorCapture(),
                isDraw ? "Draw" : (getWinner().getName() + " wins"));
    }

    @Override
    public Doubles createByeParty() {
        log.info("Creating bye party for Match_Doubles: {}", super.errorCapture());
        return Doubles.createBye();
    }


    // --- GETTERS ---
    public Player getDoublesPlayer1() {
        log.info("Getting doubles player 1 for Match_Doubles: {}", super.errorCapture());
        return super.party1.getPlayers().getFirst();
    }

    public Player getDoublesPlayer2() {
        log.info("Getting doubles player 2 for Match_Doubles: {}", super.errorCapture());
        return super.party1.getPlayers().getLast();
    }


    // --- LOGIC ---
    public void recordDoublesPlayer_Match() {
        for (Frame<Doubles> f : this.frames) {
            Player t1p1 = f.getPlayersA().getFirst();
            Player t1p2 = f.getPlayersA().getLast();
            Player t2p1 = f.getPlayersB().getFirst();
            Player t2p2 = f.getPlayersB().getLast();
            BaseStats_Key key1 = new BaseStats_Key(super.getID(), f.getParty1().getID());
            BaseStats_Key key2 = new BaseStats_Key(super.getID(), f.getParty2().getID());
            BaseStats_Service.applyEvent(key1, StatField.MATCH_TOTAL, t1p1);
            BaseStats_Service.applyEvent(key1, StatField.MATCH_TOTAL, t1p2);
            BaseStats_Service.applyEvent(key2, StatField.MATCH_TOTAL, t2p1);
            BaseStats_Service.applyEvent(key2, StatField.MATCH_TOTAL, t2p2);
            if (super.isDraw) {
                BaseStats_Service.applyEvent(key1, StatField.MATCH_DRAW, t1p1);
                BaseStats_Service.applyEvent(key1, StatField.MATCH_DRAW, t1p2);
                BaseStats_Service.applyEvent(key2, StatField.MATCH_DRAW, t2p1);
                BaseStats_Service.applyEvent(key2, StatField.MATCH_DRAW, t2p2);
            } else if (super.party1.equals(this.winner)) {
                BaseStats_Service.applyEvent(key1, StatField.MATCH_WIN, t1p1);
                BaseStats_Service.applyEvent(key1, StatField.MATCH_WIN, t1p2);
                BaseStats_Service.applyEvent(key2, StatField.MATCH_LOSS, t2p1);
                BaseStats_Service.applyEvent(key2, StatField.MATCH_LOSS, t2p2);
            } else {
                BaseStats_Service.applyEvent(key1, StatField.MATCH_LOSS, t1p1);
                BaseStats_Service.applyEvent(key1, StatField.MATCH_LOSS, t1p2);
                BaseStats_Service.applyEvent(key2, StatField.MATCH_WIN, t2p1);
                BaseStats_Service.applyEvent(key2, StatField.MATCH_WIN, t2p2);
            }
        }
        log.info("Recorded player stats for Match_Doubles: {}", super.errorCapture());
    }

    public void recordDoublesTeam_Match() {
        Doubles d1 = this.getParty1();
        Doubles d2 = this.getParty2();
        BaseStats_Service.applyEvent(matchKeyA, StatField.MATCH_TOTAL, d1);
        BaseStats_Service.applyEvent(matchKeyB, StatField.MATCH_TOTAL, d2);
        if (this.isDraw()) {
            BaseStats_Service.applyEvent(matchKeyA, StatField.MATCH_DRAW, d1);
            BaseStats_Service.applyEvent(matchKeyB, StatField.MATCH_DRAW, d2);
        } else if (super.party1.equals(this.winner)){
            BaseStats_Service.applyEvent(matchKeyA, StatField.MATCH_WIN, d1);
            BaseStats_Service.applyEvent(matchKeyB, StatField.MATCH_LOSS, d2);
        } else {
            BaseStats_Service.applyEvent(matchKeyA, StatField.MATCH_LOSS, d1);
            BaseStats_Service.applyEvent(matchKeyB, StatField.MATCH_WIN, d2);
        }
        log.info("Recorded team stats for Match_Doubles: {}", super.errorCapture());
    }

    public void recordMatch() {
        recordDoublesPlayer_Match();
        recordDoublesTeam_Match();
        updateCloud_Match();
    }
}
