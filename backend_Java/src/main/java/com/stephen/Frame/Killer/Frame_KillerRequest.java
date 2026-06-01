package com.stephen.Frame.Killer;

import java.util.List;

public class Frame_KillerRequest {

    /**
     * IDs of all players participating in this killer frame.
     * The service initialises each player with startingLives in Frame_KillerLives.
     */
    private List<Long> playerIDs;
    private int startingLives;

    public Frame_KillerRequest() {}

    public List<Long> getPlayerIDs() { return playerIDs; }
    public void setPlayerIDs(List<Long> playerIDs) { this.playerIDs = playerIDs; }

    public int getStartingLives() { return startingLives; }
    public void setStartingLives(int startingLives) { this.startingLives = startingLives; }
}
