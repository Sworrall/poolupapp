package com.stephen.Tournament;

import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("KILLER")
public class Tournament_Killer extends Tournament {

    /**
     * Whether the initial party order is randomised before fixture generation.
     * Defaults true, consistent with the convenience constructor in the original.
     */
    @Column(name = "is_random", nullable = false)
    private boolean isRandom = true;

    // --- JPA ---
    protected Tournament_Killer() {}

    // --- CONSTRUCTOR ---
    public Tournament_Killer(List<Long> partyIDs, PartyType partyType, boolean isRandom) {
        super(partyIDs, partyType);
        this.isRandom = isRandom;
    }

    // --- GETTERS ---
    public boolean isRandom() {
        return isRandom;
    }

    // --- SETTERS ---
    public void setRandom(boolean random) {
        isRandom = random;
    }
}
