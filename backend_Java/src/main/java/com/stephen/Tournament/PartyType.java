package com.stephen.Tournament;

/**
 * Identifies which party type participates in a given Tournament.
 * The service layer uses this to dispatch ID lookups to the correct repository:
 *   SINGLES  → Player_Repository
 *   DOUBLES  → Doubles_Repository
 *   TEAM     → Team_Repository
 */
public enum PartyType {
    SINGLES,
    DOUBLES,
    TEAM
}
