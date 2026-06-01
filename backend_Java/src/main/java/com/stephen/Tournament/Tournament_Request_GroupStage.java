package com.stephen.Tournament;

import java.util.List;

public class Tournament_Request_GroupStage {

    private List<Long> partyIDs;
    private PartyType partyType;
    private int groupCount;
    private int frameCount;
    private boolean isRandom = true;

    // --- JPA ---
    public Tournament_Request_GroupStage() {}

    // --- CONSTRUCTOR ---
    public Tournament_Request_GroupStage(List<Long> partyIDs, PartyType partyType,
                                         int groupCount, int frameCount, boolean isRandom) {
        this.partyIDs = partyIDs;
        this.partyType = partyType;
        this.groupCount = groupCount;
        this.frameCount = frameCount;
        this.isRandom = isRandom;
    }

    // --- GETTERS ---
    public List<Long> getPartyIDs() {
        return partyIDs;
    }

    public PartyType getPartyType() {
        return partyType;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public int getFrameCount() {
        return frameCount;
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

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }
}
