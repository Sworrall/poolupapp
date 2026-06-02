package com.stephen.Match.Team;

public class Match_Request_Team {
    private Long teamAid;
    private Long teamBid;
    private int frameCount;

    public Long getTeamAid() { return teamAid; }
    public void setTeamAid(Long Id) { this.teamAid = Id; }
    public Long getTeamBid() { return teamBid; }
    public void setTeamBid(Long Id) { this.teamBid = Id; }
    public int getFrameCount() { return frameCount; }
    public void setFrameCount(int frameCount) { this.frameCount = frameCount; }
}