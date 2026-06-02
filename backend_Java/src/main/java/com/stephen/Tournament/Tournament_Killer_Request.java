package com.stephen.Tournament;

import java.util.List;

public class Tournament_Killer_Request {

    private List<Long> partyIds;
    private PartyType partyType;
    private boolean isRandom = true;

    // --- JPA ---
    public Tournament_Killer_Request() {}

    // --- CONSTRUCTOR ---
    public Tournament_Killer_Request(List<Long> partyIds, PartyType partyType, boolean isRandom) {
        this.partyIds = partyIds;
        this.partyType = partyType;
        this.isRandom = isRandom;
    }

    // --- GETTERS ---
    public List<Long> getPartyIds() {
        return partyIds;
    }

    public PartyType getPartyType() {
        return partyType;
    }

    public boolean isRandom() {
        return isRandom;
    }

    // --- SETTERS ---
    public void setPartyIds(List<Long> partyIds) {
        this.partyIds = partyIds;
    }

    public void setPartyType(PartyType partyType) {
        this.partyType = partyType;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }
}
