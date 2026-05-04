package com.stephen.Tournament;

import java.util.ArrayList;

import com.stephen.FireBase.Tournament_Repository;
import com.stephen.Functions.ID;
import com.stephen.Match.Match;
import com.stephen.BaseStats.StatHolder;
import com.stephen.Team.Team;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public abstract class Tournament<S extends StatHolder<S>> extends ID {
    protected ArrayList<S> partyList;
    protected ArrayList<Match<S>> matchList;
    protected S place1;
    protected S place2;
    protected S place3;
    protected S place4;
    protected boolean isComplete;
    private static final Logger log = LoggerFactory.getLogger(Tournament.class);


    // --- CONSTRUCTOR ---
    public Tournament(ArrayList<S> participants) {
        super();
        this.partyList = participants;
        this.matchList = new ArrayList<>();
        isComplete = false;
        log.info("Tournament created with ID: {}", super.getID());
    }


    // --- FIRESTORE ---
    public void updateCloud_Tournament(){
        Tournament_Repository<S> tournamentRepository = new Tournament_Repository<>(this);
        tournamentRepository.saveTournament(this);
    }


    // --- GETTERS
    public ArrayList<S>getAllParties(){
        log.info("Getting all parties in the tournament...");
        return this.partyList;
    }

    public ArrayList<Match<S>> getMatches(){
        log.info("Getting all matches in the tournament...");
        return this.matchList;
    }

    public String getTournamentType() {
        if (this instanceof Tournament_GroupStage<S>) {
            return "Group_Stage";
        } else if (this instanceof Tournament_Killer<S>) {
            return "Killer";
        } else if (this instanceof Tournament_KO<S>) {
            return "KO";
        } else if (this instanceof Tournament_RoundRobin<S>) {
            return "RoundRobin";
        } else {
            return "Unknown";
        }
    }

    public S getPlace1(){
        log.info("Getting place 1 in the tournament...");
        return this.place1;
    }

    public S getPlace2(){
        log.info("Getting place 2 in the tournament...");
        return this.place2;
    }

    public S getPlace3(){
        log.info("Getting place 3 in the tournament...");
        return this.place3;
    }

    public S getPlace4(){
        log.info("Getting place 4 in the tournament...");
        return this.place4;
    }


    // --- SETTERS ---
    public void setPlace1(S place1){
        log.info("Setting place 1 in the tournament...");
        this.place1 = place1;
    }

    public void setPlace2(S place2){
        log.info("Setting place 2 in the tournament...");
        this.place2 = place2;
    }

    public void setPlace3(S place3){
        log.info("Setting place 3 in the tournament...");
        this.place3 = place3;
    }

    public void setPlace4(S place4){
        log.info("Setting place 4 in the tournament...");
        this.place4 = place4;
    }

    public boolean isComplete() {
        log.info("Checking if tournament is complete...");
        return isComplete;
    }


    // --- MISC ---
    public void setPositions(ArrayList<S> positions){
        log.info("Setting positions for the tournament...");
        if (positions.size() < 4) {
            log.error("Not enough positions provided. Need at least 4.");
            throw new IllegalArgumentException("Need at least 4 positions");
        }
        this.place1 = positions.get(0);
        this.place2 = positions.get(1);
        this.place3 = positions.get(2);
        this.place4 = positions.get(3);
    }
}