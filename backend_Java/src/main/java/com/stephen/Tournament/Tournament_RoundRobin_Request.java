package com.stephen.Tournament;

import java.util.List;

public class Tournament_RoundRobin_Request {

    private List<Long> partyIds;
    private PartyType partyType;
    private int frameCount;

    // --- JPA ---
    public Tournament_RoundRobin_Request() {}

    // --- CONSTRUCTOR ---
    public Tournament_RoundRobin_Request(List<Long> partyIds, PartyType partyType, int frameCount) {
        this.partyIds = partyIds;
        this.partyType = partyType;
        this.frameCount = frameCount;
    }

    // --- GETTERS ---
    public List<Long> getPartyIds() {
        return partyIds;
    }

    public PartyType getPartyType() {
        return partyType;
    }

    public int getFrameCount() {
        return frameCount;
    }

    // --- SETTERS ---
    public void setPartyIds(List<Long> partyIds) {
        this.partyIds = partyIds;
    }

    public void setPartyType(PartyType partyType) {
        this.partyType = partyType;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }
}
