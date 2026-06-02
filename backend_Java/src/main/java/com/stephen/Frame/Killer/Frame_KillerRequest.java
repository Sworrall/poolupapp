package com.stephen.Frame.Killer;

import java.util.List;

public class Frame_KillerRequest {

    /**
     * Ids of all players participating in this killer frame.
     * The service initialises each player with startingLives in Frame_KillerLives.
     */
    private List<Long> playerIds;
    private int startingLives;

    public Frame_KillerRequest() {}

    public List<Long> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<Long> playerIds) { this.playerIds = playerIds; }

    public int getStartingLives() { return startingLives; }
    public void setStartingLives(int startingLives) { this.startingLives = startingLives; }
}
