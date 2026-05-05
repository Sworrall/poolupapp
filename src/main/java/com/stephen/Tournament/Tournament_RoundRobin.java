package com.stephen.Tournament;

import java.util.ArrayList;
import java.util.Collections;
import com.stephen.BaseStats.StatField;
import com.stephen.FireBase.Tournament_Repository;
import com.stephen.Leaderboard.Leaderboard;
import com.stephen.Match.Match;
import com.stephen.Match.MatchFactory.Match_Factory;
import com.stephen.Leaderboard.Ranking_Points;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class Tournament_RoundRobin <S extends StatHolder<S>> extends Tournament<S>{
    private final int frameCount;
    private final Match_Factory<S> matchFactory;
    private final Leaderboard<S> leaderboard;
    private final Ranking_Points<S> rankingStrategy;
    private static final Logger log = LoggerFactory.getLogger(Tournament_RoundRobin.class);


    // --- CONSTRUCTOR ---
    public Tournament_RoundRobin(ArrayList<S> partyList, int frameCount, Match_Factory<S> matchFactory) {
        super(partyList);
        this.frameCount = frameCount;
        this.matchFactory = matchFactory;
        this.rankingStrategy = new Ranking_Points<>();
        this.leaderboard = new Leaderboard<>(partyList, super.getID(), rankingStrategy);
        generateTeamList();
        generateFixturesRR();
    }


    // --- FIREBASE ---
    public void updateCloud_Tournament(){
        Tournament_Repository<S> tournamentRepository = new Tournament_Repository<>(this);
        tournamentRepository.saveTournament(this);
        log.info("Cloud Tournament updated");
    }

    // --- FUNCTIONS ---
    public void generateTeamList() {
        if(super.partyList.size() % 2 == 1) partyList.add(partyList.getFirst().createByeParty());
        Collections.shuffle(partyList);
    }

    public void generateFixturesRR(){
        int size = super.partyList.size();
        for (int i = 0; i < size; i++) {
            ArrayList<Match<S>> fixturesList = new ArrayList<>();
            for (int j = i+1; j < size; j++) {
                fixturesList.add(matchFactory.createMatch(super.partyList.get(i), super.partyList.get(j), frameCount));
            }
            super.matchList.add(fixturesList);
        }
        Collections.shuffle(matchList);
        log.info("Generated {} RoundRobin fixtures", matchList.size());
    }

    public ArrayList<S> playAll() {
        ArrayList<S> winners = new ArrayList<>();
        for (ArrayList<Match<S>> fixturesList : super.matchList) {
            for (Match<S> m : fixturesList) {
                if(!m.isPlayed()){
                    m.playMatch();
                    winners.add(m.getWinner());
                }
            }
        }
        return winners;
    }

    public boolean playAllCheck() {
        for (ArrayList<Match<S>> fixturesList : super.matchList) {
            for (Match<S> m : fixturesList) {
                if(!m.isPlayed()){
                    log.warn("Not all matches have been played. allPlayCheck failed.");
                    return false;
                }
            }
        }
        return true;
    }

    public ArrayList<S> getPremote(int premoteAmount){
        if(playAllCheck()){
            ArrayList<S> Premoted = ((Ranking_Points<S>) leaderboard.getStrategy()).rank(getAllParties(), super.getID(), StatField.MATCH_WIN);
            return new ArrayList<>(Premoted.subList(0, premoteAmount));
        }else{
            log.warn("Not all matches have been played. getPremoted failed.");
            throw new IllegalStateException("Not all matches have been played. getPremoted failed.");
        }
    }
    
    public ArrayList<S> getDemote(int DemoteAmount){
        if(playAllCheck()){
            ArrayList<S> Demoted = ((Ranking_Points<S>) leaderboard.getStrategy()).rank(getAllParties(), super.getID(), StatField.MATCH_WIN);
            return new ArrayList<>(Demoted.subList(Demoted.size() - DemoteAmount, Demoted.size()));
        }else{
            log.warn("Not all matches have been played. getDemote failed.");
            throw new IllegalStateException("Not all matches have been played. getDemote failed.");
        }
    }
}
