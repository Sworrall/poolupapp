package com.stephen.Frame.Killer;

public class Frame_KillerResultRequest {

    /**
     * The player whose lives are being updated this frame.
     */
    private Long playerID;

    /**
     * Lives remaining for this player after the frame.
     * When this reaches 0 the player is eliminated — enforced by the service.
     */
    private int livesRemaining;

    private boolean breakDish;

    public Frame_KillerResultRequest() {}

    public Long getPlayerID() { return playerID; }
    public void setPlayerID(Long playerID) { this.playerID = playerID; }

    public int getLivesRemaining() { return livesRemaining; }
    public void setLivesRemaining(int livesRemaining) { this.livesRemaining = livesRemaining; }

    public boolean isBreakDish() { return breakDish; }
    public void setBreakDish(boolean breakDish) { this.breakDish = breakDish; }
}
