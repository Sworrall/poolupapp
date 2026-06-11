package com.stephen.Frame;

public class Frame_KillerResultRequest {

    /**
     * The player whose lives are being updated this frame.
     */
    private Long playerId;

    /**
     * Lives remaining for this player after the frame.
     * When this reaches 0 the player is eliminated — enforced by the service.
     */
    private int livesRemaining;

    private boolean breakDish;

    public Frame_KillerResultRequest() {}

    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long id) { this.playerId = id; }

    public int getLivesRemaining() { return livesRemaining; }
    public void setLivesRemaining(int livesRemaining) { this.livesRemaining = livesRemaining; }

    public boolean isBreakDish() { return breakDish; }
    public void setBreakDish(boolean breakDish) { this.breakDish = breakDish; }
}
