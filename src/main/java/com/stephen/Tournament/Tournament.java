package com.stephen.Tournament;

import java.util.ArrayList;
import com.stephen.FireBase.Tournament_Repository;
import com.stephen.Functions.ID;
import com.stephen.Match.Match;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public abstract class Tournament<S extends StatHolder<S>> extends ID {
    protected ArrayList<S> partyList;
    protected ArrayList<ArrayList<Match<S>>> matchList;
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
    }


    // --- ABSTRACT ---
    public abstract void playOutTournament();

    public abstract void generatePartyList();


    // --- FIRESTORE ---
    public void updateCloud_Tournament(){
        Tournament_Repository<S> tournamentRepository = new Tournament_Repository<>(this);
        tournamentRepository.saveTournament(this);
        log.info("Cloud Tournament updated.");
    }


    // --- GETTERS
    public ArrayList<S>getAllParties(){
        return this.partyList;
    }

    public ArrayList<ArrayList<Match<S>>> getMatches(){
        return this.matchList;
    }

    public String getTournamentType() {
        return switch (this) {
            case Tournament_GroupStage<S> sTournamentGroupStage -> "Group_Stage";
            case Tournament_Killer<S> sTournamentKiller -> "Killer";
            case Tournament_KO<S> sTournamentKo -> "KO";
            case Tournament_RoundRobin<S> sTournamentRoundRobin -> "RoundRobin";
            default -> "Unknown";
        };
    }

    public S getPlace1(){
        return this.place1;
    }

    public S getPlace2(){
        return this.place2;
    }

    public S getPlace3(){
        return this.place3;
    }

    public S getPlace4(){
        return this.place4;
    }


    // --- SETTERS ---
    public void setPlace1(S place1){
        this.place1 = place1;
    }

    public void setPlace2(S place2){
        this.place2 = place2;
    }

    public void setPlace3(S place3){
        this.place3 = place3;
    }

    public void setPlace4(S place4){
        this.place4 = place4;
    }

    public boolean isComplete() {
        return isComplete;
    }


    // --- LOGIC ---
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

    public S createByeParty() {
        return this.partyList.getFirst().createByeParty();
    }
}