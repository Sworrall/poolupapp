package com.stephen.Match;

public class Match_Request_Team {
    private Long teamAid;
    private Long teamBid;
    private int frameCount;

    public Long getTeamAid() { return teamAid; }
    public void setTeamAid(Long id) { this.teamAid = id; }
    public Long getTeamBid() { return teamBid; }
    public void setTeamBid(Long id) { this.teamBid = id; }
    public int getFrameCount() { return frameCount; }
    public void setFrameCount(int frameCount) { this.frameCount = frameCount; }
}