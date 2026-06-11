package com.stephen.Team;

import java.time.Instant;
import java.util.List;

public class Team_DetailResponse {

    private Long id;
    private String teamName;
    private String captainName;
    private Long captainId;
    private List<Team_PlayerSummaryResponse> players;
    private String address;
    private Long phoneNumber;
    private boolean isBye;
    private String firebaseUid;
    private Instant createdAt;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCaptainName() {
        return captainName;
    }
    public void setCaptainName(String captainName) {
        this.captainName = captainName;
    }

    public Long getCaptainId() {
        return captainId;
    }
    public void setCaptainId(Long captainId) {
        this.captainId = captainId;
    }

    public List<Team_PlayerSummaryResponse> getPlayers() {
        return players;
    }
    public void setPlayers(List<Team_PlayerSummaryResponse> players) {
        this.players = players;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isBye() {
        return isBye;
    }
    public void setBye(boolean bye) {
        isBye = bye;
    }

    public String getFirebaseUid() {
        return firebaseUid;
    }
    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}