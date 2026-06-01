package com.stephen.Tournament;

import java.util.List;

public class Tournament_Request_Killer {

    private List<Long> partyIDs;
    private PartyType partyType;
    private boolean isRandom = true;

    // --- JPA ---
    public Tournament_Request_Killer() {}

    // --- CONSTRUCTOR ---
    public Tournament_Request_Killer(List<Long> partyIDs, PartyType partyType, boolean isRandom) {
        this.partyIDs = partyIDs;
        this.partyType = partyType;
        this.isRandom = isRandom;
    }

    // --- GETTERS ---
    public List<Long> getPartyIDs() {
        return partyIDs;
    }

    public PartyType getPartyType() {
        return partyType;
    }

    public boolean isRandom() {
        return isRandom;
    }

    // --- SETTERS ---
    public void setPartyIDs(List<Long> partyIDs) {
        this.partyIDs = partyIDs;
    }

    public void setPartyType(PartyType partyType) {
        this.partyType = partyType;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }
}
