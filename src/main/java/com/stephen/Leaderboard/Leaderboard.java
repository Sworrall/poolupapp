package com.stephen.Leaderboard;

import java.util.ArrayList;
import com.stephen.BaseStats.StatField;
import com.stephen.BaseStats.StatHolder;


public class Leaderboard<S extends StatHolder<S>> {
    private ArrayList<S> ranked;
    private final int eventID;
    private final Ranking<S> strategy;


    // --- CONSTRUCTOR ---
    public Leaderboard(ArrayList<S> parties, int eventID, Ranking<S> strategy) {
        this.ranked = new ArrayList<>(parties);
        this.eventID = eventID;
        this.strategy = strategy;
    }


    // --- CORE API ---
    public void rank() {
        this.ranked = strategy.rank(this.ranked, eventID, StatField.MATCH_WIN);
    }

    public Ranking<S> getStrategy() { 
        return strategy;
    }

    public ArrayList<S> getRanked() { 
        return ranked;
    }

    public ArrayList<S> getTop(int n) { 
        return new ArrayList<>(ranked.subList(0, n));
    }
    
    public ArrayList<S> getBottom(int n) { 
        return new ArrayList<>(ranked.subList(ranked.size() - n, ranked.size()));
    }
    
    public ArrayList<S> getPromoted(int n) { 
        return getTop(n);
     }
    
    public ArrayList<S> getDemoted(int n) {
        return getBottom(n);}
}