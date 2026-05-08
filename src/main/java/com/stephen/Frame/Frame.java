package com.stephen.Frame;

import java.util.ArrayList;

import com.stephen.BaseStats.BaseStats;
import com.stephen.FireBase.Frame_Repository;
import com.stephen.Functions.ID;
import com.stephen.Player.Player;
import com.stephen.BaseStats.StatHolder;
import com.stephen.Functions.UserInput;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public abstract class Frame<S extends StatHolder<S>> extends ID {
    private final S party1;
    private final S party2;
    private S winner;
    private S loser;
    private boolean breakDish;
    private boolean isBye;
    private boolean isPlayed;
    private static final Logger log = LoggerFactory.getLogger(Frame.class);


    // --- CONSTRUCTOR ---
    public Frame(S p1, S p2) {
        super();
        this.party1 = p1;
        this.party2 = p2;
        breakDish = false;
        isBye = false;
        isPlayed = false;
    }


    // --- FIREBASE ---
    public void updateCloud_Frame(){
        Frame_Repository<S> frameRepo = new Frame_Repository<>();
        frameRepo.saveFrame(this);
        log.info("Updated cloud frame");
    }


    // --- ABSTRACT METHODS ---
    public abstract void recordFrame();

    public abstract ArrayList<Player> getPlayersA();
    
    public abstract ArrayList<Player> getPlayersB();


    // --- GETTERS ---
    public S getParty1(){
        return party1;
    }

    public S getParty2(){
        return party2;
    }

    public S getWinner(){
        if(!isPlayed){
            log.warn("Attempted to get winner for Frame ID: {} but match isn't played", this.getID());
            throw new IllegalArgumentException("No winner as match isn't played");
        }else{
            return winner;
        }
    }

    public S getLoser(){
        if(!isPlayed){
            log.warn("Attempted to get loser for Frame ID: {} but match isn't played", this.getID());
            throw new IllegalArgumentException("No loser as match isn't played");
        }else{
            return loser;
        }
    }

    public String getFrameType() {
        return switch (this) {
            case Frame_Killer Frame_Killer -> "Killer";
            case Frame_Doubles Frame_Doubles -> "Doubles";
            case Frame_Team Frame_Team -> "Team";
            case Frame_Singles Frame_Singles -> "Singles";
            default -> "Unknown";
        };
    }

    public boolean isBye(){
        return isBye;
    }

    public boolean isPlayed(){
        return isPlayed;
    }

    public boolean isBreakDish(){
        return breakDish;
    }


    // --- SETTERS ---
    public void setWinner(S s){
        winner = s;
    }

    public void setLoser(S s){
        loser = s;
    }


    // --- LOGIC ---
    public void playOutFrame(){
        handleBye();
        if(!isBye){
            this.playFrame();
        }
        this.recordFrame();
        this.updateCloud_Frame();
        log.info("Frame Played: {}", this.getID());
    }

    public void handleBye(){
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
        log.info("Bye handled for Frame ID: {}. isBye: {}, isPlayed: {}", this.getID(), isBye, isPlayed);
    }

    public void playFrame(){
        if(UserInput.getFrameResult()){
            winner = this.party1;
            loser = this.party2;
        }else{
            loser = this.party1;
            winner = this.party2;
        }
        if(UserInput.getBreakDish()){
            breakDish = true;
        }
        isPlayed = true;
        log.info("Frame played for Frame ID: {}. Winner: {}, Loser: {}, BreakDish: {}", this.getID(), winner.getName(), loser.getName(), breakDish);
    }
}
