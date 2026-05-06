package com.stephen.Tournament;

import java.util.ArrayList;
import java.util.Collections;
import com.stephen.BaseStats.StatField;
import com.stephen.FireBase.Tournament_Repository;
import com.stephen.Functions.Functions;
import com.stephen.Leaderboard.Leaderboard;
import com.stephen.Match.Match;
import com.stephen.Match.MatchFactory.Match_Factory;
import com.stephen.Leaderboard.Ranking_Elimination;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class Tournament_KO<S extends StatHolder<S>> extends Tournament<S> {
    private final int frameCount;
    private final Match_Factory<S> matchFactory;
    private final Leaderboard<S> leaderboard;
    private final Ranking_Elimination<S> eliminationStrategy;
    private static final Logger log = LoggerFactory.getLogger(Tournament_KO.class);


    // todo - 1st place 2nd place 3rd place 4th place
    // --- CONSTRUCTOR ---
    public Tournament_KO(ArrayList<S> partyList, int frameCount, Match_Factory<S> matchFactory){
        super(partyList);
        this.frameCount = frameCount;
        this.matchFactory = matchFactory;
        this.eliminationStrategy = new Ranking_Elimination<>();
        this.leaderboard = new Leaderboard<>(partyList, super.getID(), eliminationStrategy);
        generatePartyList();
        if (super.getAllParties().size() < 4) {
            log.error("Not enough Parties to create this tournament. Minimum 4 parties required, but only {} provided.", super.getAllParties().size());
            throw new IllegalStateException("Not enough Parties to create this tournament");
        }
        updateCloud_Tournament();
    }


    // --- FIREBASE ---
    public void updateCloud_Tournament(){
        Tournament_Repository<S> tournamentRepository = new Tournament_Repository<>(this);
        tournamentRepository.saveTournament(this);
        log.info("Cloud Tournament updated");
    }


    // --- GETTERS ---
    public int getRounds() {
        int rounds = 0;
        int size = super.getAllParties().size();
        while (size > 1) {
            rounds++;
            size = size / 2;
        }
        return rounds;
    }


    // --- LOGIC ---
    public void generatePartyList() {
        ArrayList<S> parties = super.getAllParties();
        int i = 0;
        while (!Functions.calcPowerOf2(super.getAllParties().size())) {
            parties.add(parties.getFirst().createByeParty());
            i++;
        }
        Collections.shuffle(super.getAllParties());
        super.partyList = parties;
        log.info("Generated parties: {}. Total parties: {}", i, parties.size());
    }

    public ArrayList<Match<S>> generateKORoundFixtures(ArrayList<S> partyList) {
        ArrayList<Match<S>> matchList = new ArrayList<>();
        int i = 0;
        for (int j = 0; j < partyList.size(); j += 2) {
            while(super.matchList.get(i).isEmpty()) {
                i++;
            }
            Match<S> match = matchFactory.createMatch(partyList.get(j), partyList.get(j + 1), this.frameCount);
            matchList.add(match);
        }
        super.matchList.add(matchList);
        updateCloud_Tournament();
        log.info("Generated KO Round Fixtures");
        return matchList;
    }

    public ArrayList<S> playRound(ArrayList<Match<S>> matchList) {
        ArrayList<S> partiesThrough = new ArrayList<>();
        for (Match<S> m : matchList) {
            m.playMatch();
            partiesThrough.add(m.getWinner());
        }
        updateCloud_Tournament();
        log.info("Round complete. {} through {} matches played", partiesThrough.size(), matchList.size());
        return partiesThrough;
    }

    public void simPlayTournament() {
        ArrayList<Match<S>> generatedMatches = generateKORoundFixtures(partyList);
        ArrayList<S> throughParties = playRound(generatedMatches);
        for (int i = 1; i < getRounds(); i++) {
            generatedMatches = generateKORoundFixtures(throughParties);
            throughParties = playRound(generatedMatches);
        }
        leaderboard.rank();
        ArrayList<S> winnerBracket = ((Ranking_Elimination<S>) leaderboard.getStrategy()).getWinnerBracket(partyList, super.getID(), StatField.MATCH_WIN);
        ArrayList<S> loserBracket = ((Ranking_Elimination<S>) leaderboard.getStrategy()).getLoserBracket(partyList, super.getID(), StatField.MATCH_LOSS);
        if (throughParties.size() == 1) {
            setPlace1(throughParties.getFirst());
        }
        updateCloud_Tournament();
        log.info("Tournament simulation complete. Winner: {}", throughParties.getFirst().getName());
    }
}