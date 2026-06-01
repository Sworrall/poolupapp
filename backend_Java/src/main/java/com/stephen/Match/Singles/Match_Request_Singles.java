package com.stephen.Match.Singles;

public class Match_Request_Singles {
    private Long playerAID;
    private Long playerBID;
    private Long teamAID;
    private Long teamBID;
    private int frameCount;

    public Long getPlayerAID() { return playerAID; }
    public void setPlayerAID(Long ID) { this.playerAID = ID; }
    public Long getPlayerBID() { return playerBID; }
    public void setPlayerBID(Long ID) { this.playerBID = ID; }
    public Long getTeamAID() { return teamAID; }
    public void setTeamAID(Long ID) { this.teamAID = ID; }
    public Long getTeamBID() { return teamBID; }
    public void setTeamBID(Long ID) { this.teamBID = ID; }
    public int getFrameCount() { return frameCount; }
    public void setFrameCount(int frameCount) { this.frameCount = frameCount; }
}