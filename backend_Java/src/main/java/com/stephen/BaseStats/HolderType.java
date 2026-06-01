package com.stephen.BaseStats;

/**
 * Identifies which party type a StatEntry belongs to.
 * Used alongside holderId to look up stats for the correct entity.
 *   SINGLES → Player
 *   DOUBLES → Doubles
 *   TEAM    → Team
 */
public enum HolderType {
    SINGLES,
    DOUBLES,
    TEAM
}
