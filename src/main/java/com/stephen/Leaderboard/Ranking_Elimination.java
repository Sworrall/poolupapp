package com.stephen.Leaderboard;

import java.util.ArrayList;
import java.util.Comparator;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.StatField;
import com.stephen.BaseStats.StatHolder;


public class Ranking_Elimination<S extends StatHolder<S>> implements Ranking<S> {


    @Override
    public ArrayList<S> rank(ArrayList<S> parties, int eventID, StatField field) {
        ArrayList<S> sorted = new ArrayList<>(parties);
        sorted.sort(Comparator.comparingInt((S s) -> getStat(s, eventID, field)));
        return sorted;
    }


    // --- GETTERS ---
    public ArrayList<S> getWinnerBracket(ArrayList<S> parties, int eventID, StatField field) {
        ArrayList<S> ranked = rank(parties, eventID, field);
        return new ArrayList<>(ranked.stream().filter(s -> getStat(s, eventID, field) == 0).toList());
    }

    public ArrayList<S> getLoserBracket(ArrayList<S> parties, int eventID, StatField field) {
        ArrayList<S> ranked = rank(parties, eventID, field);
        return new ArrayList<>(ranked.stream().filter(s -> getStat(s, eventID, field) == 1).toList());
    }

    public ArrayList<S> getEliminated(ArrayList<S> parties, int eventID, StatField field) {
        ArrayList<S> ranked = rank(parties, eventID, field);
        return new ArrayList<>(ranked.stream().filter(s -> getStat(s, eventID, field) >= 2).toList());
    }

    private int getStat(S s, int eventID, StatField field) {
        BaseStats_Key key = new BaseStats_Key(eventID, s.getID());
        return s.getOrCreateStats(key).get(field);
    }
}