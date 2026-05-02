package com.stephen;

import java.util.ArrayList;
import java.util.Comparator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Ranking_Elimination<S extends StatHolder<S>> implements Ranking<S> {
    private static final Logger log = LoggerFactory.getLogger(Ranking_Elimination.class);


    // todo fix this (MATCH_LOSS)

    @Override
    public ArrayList<S> rank(ArrayList<S> parties, int eventID) {
        ArrayList<S> sorted = new ArrayList<>(parties);
        sorted.sort(Comparator
            .comparingInt((S s) -> getStat(s, eventID, StatField.MATCH_LOSS))
        );
        log.info("Ranking_Elimination: Ranked {} parties for event {}", sorted.size(), eventID);
        return sorted;
    }

    public ArrayList<S> getWinnerBracket(ArrayList<S> parties, int eventID) {
        ArrayList<S> ranked = rank(parties, eventID);
        log.info("Ranking_Elimination: Winner bracket has {} parties for event {}", ranked.size(), eventID);
        return new ArrayList<>(ranked.stream()
            .filter(s -> getStat(s, eventID, StatField.MATCH_LOSS) == 0)
            .toList());
    }

    public ArrayList<S> getLoserBracket(ArrayList<S> parties, int eventID) {
        ArrayList<S> ranked = rank(parties, eventID);
        log.info("Ranking_Elimination: Loser bracket has {} parties for event {}", ranked.size(), eventID);
        return new ArrayList<>(ranked.stream()
            .filter(s -> getStat(s, eventID, StatField.MATCH_LOSS) == 1)
            .toList());
    }

    public ArrayList<S> getEliminated(ArrayList<S> parties, int eventID) {
        ArrayList<S> ranked = rank(parties, eventID);
        log.info("Ranking_Elimination: Eliminated has {} parties for event {}", ranked.size(), eventID);
        return new ArrayList<>(ranked.stream()
            .filter(s -> getStat(s, eventID, StatField.MATCH_LOSS) >= 2)
            .toList());
    }

    private int getStat(S s, int eventID, StatField field) {
        BaseStats_Key key = new BaseStats_Key(eventID, s.getID());
        log.info("Ranking_Elimination: Getting stat for party {} in event {} with key {}", s.getID(), eventID, key);
        return s.getOrCreateStats(key).get(field);
    }
}