package com.stephen.Frame;


import java.util.ArrayList;

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
        log.info("Frame created with ID: {}", this.getID());
    }


    // --- FIREBASE ---
    public void updateCloud_Frame(){
        Frame_Repository<S> frameRepo = new Frame_Repository<>();
        frameRepo.saveFrame(this);
    }


    // --- ABSTRACT METHODS ---
    public abstract void PlayOutFrame();

    public abstract void recordFrame();

    public abstract ArrayList<Player> getPlayersA();
    
    public abstract ArrayList<Player> getPlayersB();


    // --- GETTERS ---
    public S getParty1(){
        log.info("Getting party1 for Frame ID: {}", this.getID());
        return party1;
    }

    public S getParty2(){
        log.info("Getting party2 for Frame ID: {}", this.getID());
        return party2;
    }

    public S getWinner(){
        log.info("Getting winner for Frame ID: {}", this.getID());
        if(!isPlayed){
            log.warn("Attempted to get winner for Frame ID: {} but match isn't played", this.getID());
            throw new IllegalArgumentException("No winner as match isn't played");
        }else{
            return winner;
        }
    }

    public S getLoser(){
        log.info("Getting loser for Frame ID: {}", this.getID());
        if(!isPlayed){
            log.warn("Attempted to get loser for Frame ID: {} but match isn't played", this.getID());
            throw new IllegalArgumentException("No loser as match isn't played");
        }else{
            return loser;
        }
    }

    public String getFrameType() {
        if (this instanceof Frame_Killer) {
            return "Killer";
        } else if (this instanceof Frame_Doubles) {
            return "Doubles";
        } else if (this instanceof Frame_Team) {
            return "Team";
        } else if (this instanceof Frame_Singles) {
            return "Singles";
        } else {
            return "Unknown";
        }
    }

    public boolean isBye(){
        log.info("Getting isBye for Frame ID: {}", this.getID());
        return isBye;
    }

    public boolean isPlayed(){
        log.info("Getting isPlayed for Frame ID: {}", this.getID());
        return isPlayed;
    }

    public boolean isBreakDish(){
        log.info("Getting isBreakDish for Frame ID: {}", this.getID());
        return breakDish;
    }


    // --- SETTERS ---
    public void setWinner(S s){
        log.info("Setting winner for Frame ID: {}", this.getID());
        winner = s;
    }

    public void setLoser(S s){
        log.info("Setting loser for Frame ID: {}", this.getID());
        loser = s;
    }


    // --- LOGIC ---
    public void handleBye(S home ,S away){
        log.info("Handling bye for Frame ID: {}", this.getID());
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
        updateCloud_Frame();
        log.info("Bye handled for Frame ID: {}. isBye: {}, isPlayed: {}", this.getID(), isBye, isPlayed);
    }

    public void playFrame(){
        log.info("Playing frame for Frame ID: {}", this.getID());
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
        recordFrame();
        log.info("Frame played for Frame ID: {}. Winner: {}, Loser: {}, BreakDish: {}", this.getID(), winner.getName(), loser.getName(), breakDish);
    }
}
