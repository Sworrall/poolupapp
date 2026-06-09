package com.stephen.Match.DTO;

public class Match_Request_Singles {
    private Long playerAId;
    private Long playerBId;
    private Long teamAId;
    private Long teamBId;
    private int frameCount;

    public Long getPlayerAId() { return playerAId; }
    public void setPlayerAId(Long id) { this.playerAId = id; }
    public Long getPlayerBId() { return playerBId; }
    public void setPlayerBId(Long id) { this.playerBId = id; }
    public Long getTeamAId() { return teamAId; }
    public void setTeamAId(Long id) { this.teamAId = id; }
    public Long getTeamBId() { return teamBId; }
    public void setTeamBId(Long id) { this.teamBId = id; }
    public int getFrameCount() { return frameCount; }
    public void setFrameCount(int frameCount) { this.frameCount = frameCount; }
}