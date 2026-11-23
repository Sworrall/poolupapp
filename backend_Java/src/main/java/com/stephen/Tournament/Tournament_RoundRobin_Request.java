package com.stephen.Tournament;

import java.util.List;

public class Tournament_RoundRobin_Request {

    private List<Long> partyIds;
    private PartyType partyType;
    private int frameCount;
    private boolean isTwoLeg;

    // --- JPA ---
    public Tournament_RoundRobin_Request() {}

    // --- CONSTRUCTOR ---
    public Tournament_RoundRobin_Request(List<Long> partyIds, PartyType partyType, int frameCount, boolean isTwoLeg) {
        this.partyIds = partyIds;
        this.partyType = partyType;
        this.frameCount = frameCount;
        this.isTwoLeg = isTwoLeg;
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

    public boolean getIsTwoLeg(){return isTwoLeg;}

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

    public void setIsTwoLeg(boolean reverseFixtures){ this.isTwoLeg = reverseFixtures;}
}
