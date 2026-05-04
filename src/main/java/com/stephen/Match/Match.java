package com.stephen.Match;

import com.stephen.FireBase.Match_Repository;
import com.stephen.Frame.Frame;
import com.stephen.Frame.FrameFactory.FrameFactory;
import com.stephen.Functions.ID;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.util.ArrayList;


public abstract class Match <S extends StatHolder<S>> extends ID {
    protected final S party1;
    protected final S party2;
    private final int frameCount;
    ArrayList<Frame<S>> frames;
    protected S winner;
    protected S loser;
    protected boolean isPlayed;
    protected boolean isBye;
    protected boolean isDraw;
    private static final Logger log = LoggerFactory.getLogger(Match.class);
    FrameFactory<S> frameFactory = null;


    // --- CONSTRUCTOR ---
    public Match(S p1, S p2, int frameCount, FrameFactory<S> frameFactory) {
        super();
        this.party1 = p1;
        this.party2 = p2;
        this.frameCount = frameCount;
        this.frames = new ArrayList<>();
        this.frameFactory = frameFactory;
        this.isBye = false;
        this.isPlayed = false;
        this.isDraw = false;
        updateCloud_Match();
        log.info("Match created: {} with {} frames", errorCapture(), this.frameCount);
    }

    public Match(S p1, int frameCount, FrameFactory<S> frameFactory) {
        super();
        this.party1 = p1;
        this.party2 = p1.createByeParty();
        this.frameCount = frameCount;
        this.frames = new ArrayList<>();
        this.frameFactory = frameFactory;
        this.isBye = true;
        this.isPlayed = false;
        this.isDraw = false;
        updateCloud_Match();
        log.info("Match created: {} with {} frames", errorCapture(), this.frameCount);
    }

    public Match(FrameFactory<S> frameFactory) {
        super();
        this.party1 = this.createByeParty();
        this.party2 = this.createByeParty();
        this.frameCount = 0;
        this.frames = new ArrayList<>();
        this.frameFactory = frameFactory;
        this.isBye = true;
        this.isPlayed = false;
        this.isDraw = false;
        updateCloud_Match();
        log.info("Match created: {} with {} frames", errorCapture(), this.frameCount);
    }


    // --- FIREBASE ---
    public void updateCloud_Match(){
        Match_Repository<S> matchRepo = new Match_Repository<>();
        matchRepo.saveMatch(this);
        for(Frame<S> frame : frames){
            frame.recordFrame();
        }
    }


    // --- ABSTRACT METHODS ---
    public abstract void playMatch();

    public abstract S createByeParty();

    public abstract void recordMatch();


    // --- FACTORY ---
    public boolean isByeMatch(){
        handleByeMatch();
        log.info("isMatchBye check");
        return this.isBye;
    }


    // --- GETTERS ---
    public ArrayList<Frame<S>>getFrames(){
        log.info("Getting {} frames: {}. Total frames: {}", this.getMatchType(), this.errorCapture(), frames.size());
        return this.frames;
    }

    public S getParty1() {
        log.info("Getting party1: {}", party1.getName());
        return this.party1;
    }

    public S getParty2() {
        log.info("Getting party2: {}", party2.getName());
        return this.party2;
    }

    public int getFrameCount() {
        log.info("Getting frame count: {}", frameCount);
        return this.frameCount;
    }

    public String getMatchType(){
        return switch (this) {
            case Match_Singles matchSingles -> "Player";
            case Match_Doubles matchDoubles -> "Doubles";
            case Match_Team matchTeam -> "Team";
            default -> "Unknown";
        };
    }

    public boolean isPlayed() {
        log.info("Checking if match is played: {} - {}", errorCapture(), isPlayed);
        return isPlayed;
    }

    public boolean isDraw(){
        log.info("Checking if match is draw: {} - {}", errorCapture(), isDraw);
        return this.isDraw;
    }

    public boolean isBye(){
        log.info("Checking if match is a bye: {} ", isBye);
        return isBye;
    }

    public S getWinner(){
        log.info("Getting winner: {}", errorCapture());
        if(this.isPlayed){
            return this.winner;
        }else{
            log.error("Match is unplayed: {}", errorCapture());
            throw new IllegalArgumentException("Match is unplayed: cannot continue");
        }
    }

    public S getLoser(){
        log.info("Getting loser: {}", errorCapture());
        if(this.isPlayed){
            return this.loser;
        }else{
            log.error("Match is unplayed");
            throw new IllegalArgumentException("Match is unplayed");
        }
    }


    // --- LOGIC ---
    public void handleByeMatch(){
        if(party1.isBye() && party2.isBye()){
            isBye = true;
            isPlayed = true;
        }else if (party1.isBye() || party2.isBye()) {
            isBye = true;
            isPlayed = true;
            if (party1.isBye()) {
                winner = party2;
                isPlayed = true;
            } else if (party2.isBye()) {
                winner = party1;
                isPlayed = true;
            }
        }
        log.info("Handled bye match: {} - isBye: {}, isPlayed: {}", errorCapture(), isBye, isPlayed);
    }


    // --- MISC ---
    public String errorCapture(){
        return party1.getName() + " VS " + party2.getName();
    }
}