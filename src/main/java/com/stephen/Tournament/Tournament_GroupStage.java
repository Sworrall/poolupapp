package com.stephen.Tournament;

import java.util.*;
import com.stephen.BaseStats.StatField;
import com.stephen.FireBase.Tournament_Repository;
import com.stephen.Leaderboard.Leaderboard;
import com.stephen.Match.Match;
import com.stephen.Match.MatchFactory.Match_Factory;
import com.stephen.Leaderboard.Ranking_Elimination;
import com.stephen.Leaderboard.Ranking_Points;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class Tournament_GroupStage<S extends StatHolder<S>> extends Tournament<S> {
    private final int groupCount;
    private final int frameCount;
    private ArrayList<ArrayList<S>> partyGrouped;
    private final Match_Factory<S> matchFactory;
    private final Leaderboard<S> leaderboard;
    private final Ranking_Elimination<S> eliminationStrategy;
    private static final Logger log = LoggerFactory.getLogger(Tournament_GroupStage.class);


    // --- CONSTRUCTOR ---
    public Tournament_GroupStage(ArrayList<S> partyList, int groupCount, Match_Factory<S> matchFactory,int totalFrameCount, Ranking_Elimination<S> eliminationStrategy){
        super(partyList);
        this.groupCount = groupCount;
        this.frameCount = totalFrameCount;
        this.partyGrouped = new ArrayList<>();
        this.matchFactory = matchFactory;
        this.eliminationStrategy = eliminationStrategy;
        this.leaderboard = new Leaderboard<>(partyList, super.getID(), eliminationStrategy);
        if (partyList.size() < 4) {
            log.error("GroupStage Tournament initialization failed. Not enough participants: {}", partyList.size());
            throw new IllegalStateException("Not enough participants");
        }
        generatePartyList();
        generatePartyGrouping();
        generateGroupStageFixtures();
        updateCloud_Tournament();
    }


    // --- FIREBASE ---
    public void updateCloud_Tournament(){
        Tournament_Repository<S> tournamentRepository = new Tournament_Repository<>(this);
        tournamentRepository.saveTournament(this);
        log.info("Cloud Tournament updated");
    }


    // --- LOGIC ---
    public void simTournament() {
        generatePartyList();
        generatePartyGrouping();
        generateGroupStageFixtures();
        playAllGroupStage();
        super.isComplete = true;
        updateCloud_Tournament();
        log.info("Tournament completed. All matches played.");
    }

    public void generatePartyList() {
        while (super.partyList.size() < 4 * groupCount) {
            partyList.add(partyList.getFirst().createByeParty());
        }
        Collections.shuffle(partyList);
        log.info("GroupStage Party list generated and shuffled. Total parties: {}", partyList.size());
    }

    public void generatePartyGrouping() {
        ArrayList<ArrayList<S>> grouped = new ArrayList<>();
        for (int i = 0; i < groupCount; i++) {
            ArrayList<S> group = new ArrayList<>();
            for (int j = i * 4; j < (i + 1) * 4; j++) {
                group.add(partyList.get(j));
            }
            grouped.add(group);
        }
        this.partyGrouped = grouped;
        log.info("GroupStage Party grouping generated with {} groups of {}-{}.", partyGrouped.size(), partyGrouped.getFirst().size(), partyGrouped.getLast().size());
    }

    public void generateGroupStageFixtures() {
        for (ArrayList<S> group : partyGrouped) {
            ArrayList<Match<S>> matchFixtures = new ArrayList<>();
            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    S p1 = group.get(i);
                    S p2 = group.get(j);
                    matchFixtures.add(matchFactory.createMatch(p1, p2, this.frameCount));
                }
            }
            super.matchList.add(matchFixtures);
        }
        log.info("GroupStage match fixtures generated. Total matches: {}", matchList.size());
    }

    public void playAllGroupStage() {
        for (ArrayList<Match<S>> matches : super.matchList) {
            for (Match<S> m : matches) {
                m.playOutMatch();
            }
        }
        updateCloud_Tournament();
        log.info("All GroupStage matches played.");
    }

    public boolean playAllCheck() {
        ArrayList<Match<S>> unplayed = new ArrayList<>();
        for (ArrayList<Match<S>> matches : super.matchList) {
            for (Match<S> m : matches) {
                if (!m.isPlayed()) {
                    unplayed.add(m);
                }
            }
        }
        if (!unplayed.isEmpty()) {
            for (int i = 0; i < matchList.size(); i++) {
                for (Match<S> m : unplayed) {
                    System.out.println(m.errorCapture());
                }
            }
            log.warn("GroupStage playAllCheck failed. Unplayed matches found: {}", unplayed.size());
            return false;
        }
        return true;
    }

    public ArrayList<S> getPremote(int premoteAmount){
        if(playAllCheck()){
            ArrayList<S> Premoted = ((Ranking_Points<S>) leaderboard.getStrategy()).rank(getAllParties(), super.getID(), StatField.MATCH_TOTAL);
            return new ArrayList<>(Premoted.subList(0, premoteAmount));
        }
        log.info("GroupStage getPremote failed. Not all matches have been played.");
        return new ArrayList<>();
    }
    
    public ArrayList<S> getDemote(int DemoteAmount){
        if(playAllCheck()){
            ArrayList<S> Demoted = ((Ranking_Points<S>) leaderboard.getStrategy()).rank(getAllParties(), super.getID(), StatField.MATCH_TOTAL);
            return new ArrayList<>(Demoted.subList(Demoted.size() - DemoteAmount, Demoted.size()));
        }
        log.info("GroupStage getDemote failed. Not all matches have been played.");
        return new ArrayList<>();
    }
}