package com.stephen.Leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.StatField;
import com.stephen.BaseStats.StatHolder;


public class Ranking_Points<S extends StatHolder<S>> implements Ranking<S> {


    // --- CONSTRUCTOR ---
    @Override
    public ArrayList<S> rank(ArrayList<S> parties, int eventID, StatField field) {
        ArrayList<S> sorted = new ArrayList<>(parties);
        sorted.sort(Comparator
            .comparingInt((S s) -> getStat(s, eventID, StatField.MATCH_WIN))
            .thenComparingInt(s -> getStat(s, eventID, StatField.FRAME_WIN))
            .thenComparingInt(s -> getStat(s, eventID, StatField.FRAME_WIN) - getStat(s, eventID, StatField.FRAME_LOSS))
            .thenComparingInt(s -> getStat(s, eventID, StatField.FRAME_BREAK_DISH))
            .reversed()
        );
        return sorted;
    }


    // --- LOGIC ---
    private int getStat(S s, int eventID, StatField field) {
        BaseStats_Key key = new BaseStats_Key(eventID, s.getID());
        return s.getOrCreateStats(key).get(field);
    }
}