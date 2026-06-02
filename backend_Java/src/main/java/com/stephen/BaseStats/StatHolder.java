package com.stephen.BaseStats;

/**
 * Identifies a party that can accumulate stats.
 * Implemented by Player, Doubles, and Team.
 *
 * Stripped of Firebase methods (updateCloud_Attributes, updateCloud_Stats,
 * updateCloud_All, createByeParty, getStatsMap, getOrCreateStats) —
 * persistence is now the service layer's responsibility.
 *
 * Retained for leaderboard and ranking layer which needs to identify
 * and compare parties polymorphically by ID, name, and bye status.
 */
public interface StatHolder {

    Long getId();

    String getName();

    boolean isBye();
}
