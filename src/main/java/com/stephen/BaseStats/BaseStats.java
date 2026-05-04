package com.stephen.BaseStats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.EnumMap;
import java.util.Map;

public class BaseStats {
    private static final int GLOBAL = 0;
    private final Map<StatField, Integer> stats;

    private static final Logger log = LoggerFactory.getLogger(BaseStats.class);


    // --- CONSTRUCTOR ---
    public BaseStats() {
        stats = new EnumMap<>(StatField.class);
        for (StatField field : StatField.values()) {
            stats.put(field, GLOBAL);
        }
        log.info("Initialized BaseStats with default values.");
    }


    // --- CORE API ---
    public void increment(StatField field) {
        stats.put(field, stats.get(field) + 1);
        log.info("Incremented stat: {}", field);
    }

    public void add(StatField field, int value) {
        stats.put(field, stats.get(field) + value);
        log.info("Added {} to stat: {}", value, field);
    }

    public void set(StatField field, int value) {
        stats.put(field, value);
        log.info("Set stat: {} to value: {}", field, value);
    }

    public int get(StatField field) {
        log.info("Getting stat: {}", field);
        return stats.getOrDefault(field, GLOBAL);
    }
}