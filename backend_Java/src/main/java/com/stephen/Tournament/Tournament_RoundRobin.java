package com.stephen.Tournament;

import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("ROUND_ROBIN")
public class Tournament_RoundRobin extends Tournament {

    /**
     * Number of frames per match. Nullable in the DB column because
     * other subtypes share the same table, but always set for this type.
     */
    @Column(name = "frame_count")
    private Integer frameCount;

    // --- JPA ---
    protected Tournament_RoundRobin() {}

    // --- CONSTRUCTOR ---
    public Tournament_RoundRobin(List<Long> partyIDs, PartyType partyType, int frameCount) {
        super(partyIDs, partyType);
        this.frameCount = frameCount;
    }

    // --- GETTERS ---
    public Integer getFrameCount() {
        return frameCount;
    }

    // --- SETTERS ---
    public void setFrameCount(Integer frameCount) {
        this.frameCount = frameCount;
    }
}
