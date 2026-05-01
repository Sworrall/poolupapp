package com.stephen;

import java.util.ArrayList;
import java.util.Collections;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Tournament_Killer  <S extends StatHolder<S>> extends Tournament<S>{
    private final Match_Factory<S> matchFactory;
    private final boolean isRandom;
    private static final Logger log = LoggerFactory.getLogger(Tournament_Killer.class);


    // CONSTRUCTOR
    public Tournament_Killer(ArrayList<S> allParties, boolean isRandom, Match_Factory<S> matchFactory) {
        super(allParties);
        this.matchFactory = matchFactory;
        this.isRandom = isRandom;
        log.info("Random Killer Tournament created with {} parties", allParties.size());
    }

    public Tournament_Killer(ArrayList<S> allParties, Match_Factory<S> matchFactory) {
        super(allParties);
        this.matchFactory = matchFactory;
        this.isRandom = true;
        log.info("Organised Killer Tournament created with {} parties", allParties.size());
    }


    // FUNCTIONS
    public void generateTeamList() {
        if(super.partyList.size() % 2 == 1) partyList.add(partyList.getFirst().createByeParty());
        if(this.isRandom) Collections.shuffle(partyList);
        log.info("Team list generated with {} parties, is Random?: {}", partyList.size(), isRandom);
    }

    public void generateFixturesRR(int frameCount){
        int size = super.partyList.size();
        for (int i = 0; i < size; i++) {
            for (int j = i+1; j < size; j++) {
                matchList.add(matchFactory.createMatch(super.partyList.get(i), super.partyList.get(j), frameCount));
            }
        }
        if(this.isRandom) Collections.shuffle(matchList);
        log.info("Killer fixtures generated with {} matches.", matchList.size());
    }

    public ArrayList<S> playAll(ArrayList<Match<S>> matchList) {
        ArrayList<S> winners = new ArrayList<>();
        for (Match<S> m : super.matchList) {
            if(!m.isPlayed()){
                m.playMatch();
                winners.add(m.getWinner());
            }
        }
        log.info("All matches played, {} winners.", winners.size());
        return winners;
    }
}
