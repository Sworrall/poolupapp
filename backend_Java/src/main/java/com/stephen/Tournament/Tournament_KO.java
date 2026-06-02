package com.stephen.Tournament;

import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("KO")
public class Tournament_KO extends Tournament {

    /**
     * Number of frames per match in each KO round.
     */
    @Column(name = "frame_count")
    private Integer frameCount;

    // --- JPA ---
    protected Tournament_KO() {}

    // --- CONSTRUCTOR ---
    public Tournament_KO(List<Long> partyIds, PartyType partyType, int frameCount) {
        super(partyIds, partyType);
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
