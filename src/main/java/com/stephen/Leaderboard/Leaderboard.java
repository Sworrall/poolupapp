package com.stephen.Leaderboard;

import java.util.ArrayList;
import com.stephen.BaseStats.StatField;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class Leaderboard<S extends StatHolder<S>> {
    private ArrayList<S> ranked;
    private final int eventID;
    private final Ranking<S> strategy;
    private static final Logger log = LoggerFactory.getLogger(Leaderboard.class);


    // --- CONSTRUCTOR ---
    public Leaderboard(ArrayList<S> parties, int eventID, Ranking<S> strategy) {
        this.ranked = new ArrayList<>(parties);
        this.eventID = eventID;
        this.strategy = strategy;
        log.info("Initialized leaderboard with {} parties for event {}", parties.size(), eventID);
    }


    // --- CORE API ---
    public void rank() {
        this.ranked = strategy.rank(this.ranked, eventID, StatField.MATCH_WIN);
        log.info("Ranked leaderboard for event {}", eventID);
    }

    public Ranking<S> getStrategy() { 
        log.info("Retrieved ranking strategy for event {}", eventID);
        return strategy; 
    }

    public ArrayList<S> getRanked() { 
        log.info("Retrieved ranked list for event {}", eventID);
        return ranked; 
    }

    public ArrayList<S> getTop(int n) { 
        log.info("Retrieved top {} parties for event {}", n, eventID);
        return new ArrayList<>(ranked.subList(0, n)); 
    }
    
    public ArrayList<S> getBottom(int n) { 
        log.info("Retrieved bottom {} parties for event {}", n, eventID);
        return new ArrayList<>(ranked.subList(ranked.size() - n, ranked.size())); 
    }
    
    public ArrayList<S> getPromoted(int n) { 
        log.info("Retrieved promoted parties (top {}) for event {}", n, eventID);
        return getTop(n);
     }
    
    public ArrayList<S> getDemoted(int n) {
        log.info("Retrieved demoted parties (bottom {}) for event {}", n, eventID);
        return getBottom(n);}
}