package com.stephen.Leaderboard;

import java.util.ArrayList;
import java.util.Comparator;

import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.StatField;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Ranking_Points<S extends StatHolder<S>> implements Ranking<S> {

    private static final Logger log = LoggerFactory.getLogger(Ranking_Points.class);

    @Override
    public ArrayList<S> rank(ArrayList<S> parties, int eventID) {
        ArrayList<S> sorted = new ArrayList<>(parties);
        sorted.sort(Comparator
            .comparingInt((S s) -> getStat(s, eventID, StatField.MATCH_WIN))
            .thenComparingInt(s -> getStat(s, eventID, StatField.FRAME_WIN))
            .thenComparingInt(s -> getStat(s, eventID, StatField.FRAME_WIN) - getStat(s, eventID, StatField.FRAME_LOSS))
            .thenComparingInt(s -> getStat(s, eventID, StatField.FRAME_BREAK_DISH))
            .reversed()
        );
        log.info("Ranking_Points: Sorted parties based on points for eventID {}: {}", eventID, sorted);
        return sorted;
    }

    private int getStat(S s, int eventID, StatField field) {
        BaseStats_Key key = new BaseStats_Key(eventID, s.getID());
        log.info("Ranking_Points: Retrieving stat for party ID {}: eventID {}, field {}", s.getID(), eventID, field);
        return s.getOrCreateStats(key).get(field);
    }
}