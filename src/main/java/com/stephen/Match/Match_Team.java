package com.stephen.Match;

import java.util.*;
import com.stephen.Frame.Frame;
import com.stephen.Frame.FrameFactory.FrameFactory;
import com.stephen.Player.Player;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.BaseStats_Service;
import com.stephen.BaseStats.StatField;
import com.stephen.Team.Team;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class Match_Team extends Match<Team>{
    private final BaseStats_Key key1;
    private final BaseStats_Key key2;
    private final FrameFactory<Team> frameFactory;
    private static final Logger log = LoggerFactory.getLogger(Match_Team.class);


    // --- CONSTRUCTOR ---
    public Match_Team(Team teamA, Team teamB, int frameCount, FrameFactory<Team> frameFactory) {
        super(teamA, teamB, frameCount, frameFactory);
        this.key1 = new BaseStats_Key(super.getID(), teamA.getID());
        this.key2 = new BaseStats_Key(super.getID(), teamB.getID());
        this.frameFactory = frameFactory;
        this.isPlayed = false;
        this.isBye = false;
        this.isDraw = false;
        log.info("Created Match_Team: {} with {} frames.", super.errorCapture(), super.getFrameCount());
    }

    public Match_Team(Team team, int frameCount, FrameFactory<Team> frameFactory) {
        super(team, frameCount, frameFactory);
        this.key1 = new BaseStats_Key(super.getID(), team.getID());
        this.key2 = new BaseStats_Key(super.getID(), 0);
        this.frameFactory = frameFactory;
        this.isPlayed = false;
        this.isBye = true;
        this.isDraw = false;
        log.info("Created Match_Team: {} with {} frames.", super.errorCapture(), super.getFrameCount());
    }

    public Match_Team(FrameFactory<Team> frameFactory) {
        super(Team.createBye(), Team.createBye(), 0, frameFactory);
        this.key1 = new BaseStats_Key(super.getID(), 0);
        this.key2 = new BaseStats_Key(super.getID(), 0);
        this.frameFactory = frameFactory;
        this.isPlayed = false;
        this.isBye = true;
        this.isDraw = false;
        log.info("Created Match_Team: {} with {} frames.", super.errorCapture(), super.getFrameCount());
    }


    // --- INTERFACE ---
    @Override
    public Team createByeParty() {
        log.info("Creating bye party for Match_Team.");
        return new Team();
    }

    @Override
    public void playMatch(){
        handleByeMatch();
        if(!isBye){
            for (int i = 0; i < this.getFrameCount(); i++) {
                Frame<Team> f = super.frameFactory.createFrame(party1, party2);
                super.frames.add(f);
                f.playFrame();
            }
            isPlayed = true;
            long party1Wins = frames.stream().filter(f -> f.getWinner().equals(party1)).count();
            long party2Wins = frames.stream().filter(f -> f.getWinner().equals(party2)).count();
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
        recordPlayerInTeam_Match();
        recordTeam_Match();
        log.info("Played Match_Team: {} vs {}. Result: {}", party1.getName(), party2.getName(), isDraw ? "Draw" : (getWinner().getName() + " wins"));
    }


    // --- GETTERS ---
    public ArrayList<Player> getParticipantsTeamA(){
        ArrayList<Player> partyList = new ArrayList<>();
        for (Frame<Team> f : this.frames) {
            partyList.add(f.getPlayersA().getFirst());
        }
        log.info("Getting participants for Team A in Match_Team: {} vs {}. Participants: {}", party1.getName(), party2.getName(), partyList.size());
        return partyList;
    }

    public ArrayList<Player> getParticipantsTeamB(){
        ArrayList<Player> partyList = new ArrayList<>();
        for (Frame<Team> f : this.frames) {
            partyList.add(f.getPlayersB().getFirst());
        }
        log.info("Getting participants for Team B in Match_Team: {} vs {}. Participants: {}", party1.getName(), party2.getName(), partyList.size());
        return partyList;
    }


    // --- LOGIC ---
    public void recordTeam_Match() {
        BaseStats_Service.applyEvent(key1, StatField.MATCH_TOTAL, getParty1());
        BaseStats_Service.applyEvent(key2, StatField.MATCH_TOTAL, getParty2());
        if (super.isDraw) {
            BaseStats_Service.applyEvent(key1, StatField.MATCH_DRAW, getParty1());
            BaseStats_Service.applyEvent(key2, StatField.MATCH_DRAW, getParty2());
        } else if (this.getWinner().equals(super.party1)) {
            BaseStats_Service.applyEvent(key1, StatField.MATCH_WIN, getParty1());
            BaseStats_Service.applyEvent(key2, StatField.MATCH_LOSS, getParty2());
        } else {
            BaseStats_Service.applyEvent(key1, StatField.MATCH_LOSS, getParty1());
            BaseStats_Service.applyEvent(key2, StatField.MATCH_WIN, getParty2());
        }
        log.info("Recorded team stats for Match_Team: {} vs {}. Result: {}", party1.getName(), party2.getName(), isDraw ? "Draw" : (getWinner().getName() + " wins"));
    }

    public void recordPlayerInTeam_Match(){
        for (int i = 0; i < super.getFrameCount(); i++) {
            Frame<Team> f = this.getFrames().get(i);
            Player p1 = f.getPlayersA().getFirst();
            Player p2 = f.getPlayersB().getFirst();
            BaseStats_Key p1Key = new BaseStats_Key(super.getID(), f.getParty1().getID());
            BaseStats_Key p2Key = new BaseStats_Key(super.getID(), f.getParty2().getID());
            BaseStats_Service.applyEvent(p1Key, StatField.MATCH_TOTAL, p1);
            BaseStats_Service.applyEvent(p2Key, StatField.MATCH_TOTAL, p2);
            if (super.isDraw) {
                BaseStats_Service.applyEvent(p1Key, StatField.MATCH_DRAW, p1);
                BaseStats_Service.applyEvent(p2Key, StatField.MATCH_DRAW, p2);
            } else if (super.party1.equals(this.getWinner())) {
                BaseStats_Service.applyEvent(p1Key, StatField.MATCH_WIN, p1);
                BaseStats_Service.applyEvent(p2Key, StatField.MATCH_LOSS, p2);
            } else {
                BaseStats_Service.applyEvent(p1Key, StatField.MATCH_LOSS, p1);
                BaseStats_Service.applyEvent(p2Key, StatField.MATCH_WIN, p2);
            }
        }
        log.info("Recorded player stats for Match_Team: {} vs {}", party1.getName(), party2.getName());
    }

    public void recordMatch(){
        recordPlayerInTeam_Match();
        recordTeam_Match();
        updateCloud_Match();
    }
}


