package com.stephen.Team;

public class Team_ListResponse {
    private Long id;
    private String teamName;
    private String captainName;
    private boolean isBye;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setCaptainName(String s) {
        this.captainName = s;
    }

    public void setBye(boolean bye) {
        this.isBye = bye;
    }

    // getters/setters
}