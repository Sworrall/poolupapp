package com.stephen.Tournament;

import java.util.List;

public class Tournament_GroupStage_Request {

    private List<Long> partyIds;
    private PartyType partyType;
    private int groupCount;
    private int frameCount;
    private boolean isRandom = true;

    // --- JPA ---
    public Tournament_GroupStage_Request() {}

    // --- CONSTRUCTOR ---
    public Tournament_GroupStage_Request(List<Long> partyIds, PartyType partyType,
                                         int groupCount, int frameCount, boolean isRandom) {
        this.partyIds = partyIds;
        this.partyType = partyType;
        this.groupCount = groupCount;
        this.frameCount = frameCount;
        this.isRandom = isRandom;
    }

    // --- GETTERS ---
    public List<Long> getPartyIds() {
        return partyIds;
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
    public void setPartyIds(List<Long> partyIds) {
        this.partyIds = partyIds;
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
