package com.stephen.Match.Singles;

public class Match_Request_Singles {
    private Long playerAId;
    private Long playerBId;
    private Long teamAId;
    private Long teamBId;
    private int frameCount;

    public Long getPlayerAId() { return playerAId; }
    public void setPlayerAId(Long Id) { this.playerAId = Id; }
    public Long getPlayerBId() { return playerBId; }
    public void setPlayerBId(Long Id) { this.playerBId = Id; }
    public Long getTeamAId() { return teamAId; }
    public void setTeamAId(Long Id) { this.teamAId = Id; }
    public Long getTeamBId() { return teamBId; }
    public void setTeamBId(Long Id) { this.teamBId = Id; }
    public int getFrameCount() { return frameCount; }
    public void setFrameCount(int frameCount) { this.frameCount = frameCount; }
}