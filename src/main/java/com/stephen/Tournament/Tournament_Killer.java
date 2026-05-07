package com.stephen.Tournament;

import java.util.ArrayList;
import java.util.Collections;
import com.stephen.FireBase.Tournament_Repository;
import com.stephen.Match.Match;
import com.stephen.Match.MatchFactory.Match_Factory;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Tournament_Killer  <S extends StatHolder<S>> extends Tournament<S>{
    private final Match_Factory<S> matchFactory;
    private final boolean isRandom;
    private static final Logger log = LoggerFactory.getLogger(Tournament_Killer.class);


    // --- CONSTRUCTOR ---
    public Tournament_Killer(ArrayList<S> allParties, boolean isRandom, Match_Factory<S> matchFactory) {
        super(allParties);
        this.matchFactory = matchFactory;
        this.isRandom = isRandom;
        this.generateTeamList();
        updateCloud_Tournament();
    }

    public Tournament_Killer(ArrayList<S> allParties, Match_Factory<S> matchFactory) {
        super(allParties);
        this.matchFactory = matchFactory;
        this.isRandom = true;
        this.generateTeamList();
        updateCloud_Tournament();
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
        if(this.isRandom) Collections.shuffle(partyList);
        log.info("Generated Party List: {}", partyList.size());
    }

    public ArrayList<S> playAll(ArrayList<Match<S>> matchList) {
        ArrayList<S> winners = new ArrayList<>();
        for (ArrayList<Match<S>> fixturesList : super.matchList) {
            for (Match<S> m : fixturesList) {
                if(!m.isPlayed()){
                    m.playOutMatch();
                    winners.add(m.getWinner());
                }
            }
        }
        updateCloud_Tournament();
        log.info("Round completed. {} winners.", winners.size());
        return winners;
    }
}
