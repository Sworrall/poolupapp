package com.stephen.Player;

import java.util.HashMap;
import java.util.Map;

import com.stephen.BaseStats.BaseStats;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Player_BaseStats <S extends StatHolder<S>> {
    private final int playerID;
    private Map<BaseStats_Key, BaseStats> stats;
    private final int GLOBAL = 0;
    private static final Logger log = LoggerFactory.getLogger(Player_BaseStats.class);


    // --- CONSTRUCTOR ---
    public Player_BaseStats(int playerID) {
        this.playerID = playerID;
        this.stats = new HashMap<>();
        getOrCreatePlayerStats(new BaseStats_Key(GLOBAL, null));
        log.info("Created Player_BaseStats for playerID: {}", playerID);
    }


    // --- GETTERS ---
    public int getPlayerID() {
        log.info("Retrieving playerID: {}", playerID);
        return playerID;
    }

    public BaseStats getOrCreatePlayerStats(BaseStats_Key K) {
        log.info("Retrieving or creating BaseStats for key: {}", K);
        return stats.computeIfAbsent(K, _ -> new BaseStats());
    }

    public BaseStats getOrCreateStats(int eventID, Integer teamID) {
        BaseStats_Key K = new BaseStats_Key(eventID, teamID);
        log.info("Retrieving or creating BaseStats for eventID: {}, teamID: {}", eventID, teamID);
        return getOrCreatePlayerStats(K);
    }
}
