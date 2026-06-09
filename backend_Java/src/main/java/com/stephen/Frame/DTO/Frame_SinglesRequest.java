package com.stephen.Frame.DTO;

public class Frame_SinglesRequest {
    private Long playerAid;
    private Long playerBid;
    private Long teamAid;
    private Long teamBid;

    public Long getPlayerAid() { return playerAid; }
    public void setPlayerAid(Long id) { this.playerAid = id; }

    public Long getPlayerBid() { return playerBid; }
    public void setPlayerBid(Long id) { this.playerBid = id; }

    public Long getTeamAid() { return teamAid; }
    public void setTeamAid(Long id) { this.teamAid = id; }

    public Long getTeamBid() { return teamBid; }
    public void setTeamBid(Long id) { this.teamBid = id; }
}