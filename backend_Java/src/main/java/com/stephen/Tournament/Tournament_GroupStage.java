package com.stephen.Tournament;

import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("GROUP_STAGE")
public class Tournament_GroupStage extends Tournament_Entity {

    /**
     * Number of groups to split participants into.
     * The service enforces a minimum of 4 parties per group when building fixtures.
     */
    @Column(name = "group_count")
    private Integer groupCount;

    /**
     * Number of frames per match within the group stage.
     */
    @Column(name = "frame_count")
    private Integer frameCount;

    /**
     * Whether participants are randomly assigned to groups.
     */
    @Column(name = "is_random", nullable = false)
    private boolean isRandom = true;

    // --- JPA ---
    protected Tournament_GroupStage() {}

    // --- CONSTRUCTOR ---
    public Tournament_GroupStage(List<Long> partyIds, PartyType partyType,
                                 int groupCount, int frameCount, boolean isRandom) {
        super(partyIds, partyType);
        this.groupCount = groupCount;
        this.frameCount = frameCount;
        this.isRandom = isRandom;
    }

    // --- GETTERS ---
    public Integer getGroupCount() {
        return groupCount;
    }

    public Integer getFrameCount() {
        return frameCount;
    }

    public boolean isRandom() {
        return isRandom;
    }

    // --- SETTERS ---
    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
    }

    public void setFrameCount(Integer frameCount) {
        this.frameCount = frameCount;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }
}
