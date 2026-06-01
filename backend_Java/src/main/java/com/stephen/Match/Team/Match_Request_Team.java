package com.stephen.Match.Team;

public class Match_Request_Team {
    private Long teamAID;
    private Long teamBID;
    private int frameCount;

    public Long getTeamAID() { return teamAID; }
    public void setTeamAID(Long ID) { this.teamAID = ID; }
    public Long getTeamBID() { return teamBID; }
    public void setTeamBID(Long ID) { this.teamBID = ID; }
    public int getFrameCount() { return frameCount; }
    public void setFrameCount(int frameCount) { this.frameCount = frameCount; }
}