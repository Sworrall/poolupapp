package com.stephen.Tournament;

import java.util.List;

public class Tournament_Request_RoundRobin {

    private List<Long> partyIDs;
    private PartyType partyType;
    private int frameCount;

    // --- JPA ---
    public Tournament_Request_RoundRobin() {}

    // --- CONSTRUCTOR ---
    public Tournament_Request_RoundRobin(List<Long> partyIDs, PartyType partyType, int frameCount) {
        this.partyIDs = partyIDs;
        this.partyType = partyType;
        this.frameCount = frameCount;
    }

    // --- GETTERS ---
    public List<Long> getPartyIDs() {
        return partyIDs;
    }

    public PartyType getPartyType() {
        return partyType;
    }

    public int getFrameCount() {
        return frameCount;
    }

    // --- SETTERS ---
    public void setPartyIDs(List<Long> partyIDs) {
        this.partyIDs = partyIDs;
    }

    public void setPartyType(PartyType partyType) {
        this.partyType = partyType;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }
}
