package com.stephen.Tournament;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "tournament")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tournament_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Tournament {

    @Transient
    private static final Logger log = LoggerFactory.getLogger(Tournament.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    /**
     * Discriminates which party type participates in this tournament.
     * Used by the service layer to resolve partyIDs to the correct repository.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "party_type", nullable = false)
    private PartyType partyType;

    /**
     * Flat list of participant IDs. The service layer resolves these
     * against Player_Repository, Doubles_Repository, or Team_Repository
     * depending on partyType. Stored as a joined collection table to avoid
     * a fixed-width column array.
     */
    @ElementCollection
    @CollectionTable(
            name = "tournament_party",
            joinColumns = @JoinColumn(name = "tournament_id")
    )
    @Column(name = "party_id")
    @OrderColumn(name = "party_order")
    private List<Long> partyIDs = new ArrayList<>();

    // Podium — nullable until tournament is complete.
    // Resolved to full party objects by the service layer via partyType.
    @Column(name = "place1_id")
    private Long place1ID;

    @Column(name = "place2_id")
    private Long place2ID;

    @Column(name = "place3_id")
    private Long place3ID;

    @Column(name = "place4_id")
    private Long place4ID;

    @Column(name = "is_started", nullable = false)
    private boolean isStarted = false;

    @Column(name = "is_complete", nullable = false)
    private boolean isComplete = false;

    // --- JPA ---
    protected Tournament() {}

    // --- CONSTRUCTOR ---
    protected Tournament(List<Long> partyIDs, PartyType partyType) {
        this.partyIDs = new ArrayList<>(partyIDs);
        this.partyType = partyType;
    }

    // --- GETTERS ---
    public Long getID() {
        return ID;
    }

    public PartyType getPartyType() {
        return partyType;
    }

    public List<Long> getPartyIDs() {
        return partyIDs;
    }

    public Long getPlace1ID() {
        return place1ID;
    }

    public Long getPlace2ID() {
        return place2ID;
    }

    public Long getPlace3ID() {
        return place3ID;
    }

    public Long getPlace4ID() {
        return place4ID;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isComplete() {
        return isComplete;
    }

    // --- SETTERS ---
    public void setPartyIDs(List<Long> partyIDs) {
        this.partyIDs = new ArrayList<>(partyIDs);
    }

    public void setPlace1ID(Long place1ID) {
        this.place1ID = place1ID;
    }

    public void setPlace2ID(Long place2ID) {
        this.place2ID = place2ID;
    }

    public void setPlace3ID(Long place3ID) {
        this.place3ID = place3ID;
    }

    public void setPlace4ID(Long place4ID) {
        this.place4ID = place4ID;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    /**
     * Convenience setter — accepts an ordered list of at least 4 IDs.
     * Consistent with original setPositions() behaviour.
     */
    public void setPositions(List<Long> positions) {
        if (positions.size() < 4) {
            log.error("setPositions requires at least 4 IDs, received {}", positions.size());
            throw new IllegalArgumentException("Need at least 4 positions");
        }
        this.place1ID = positions.get(0);
        this.place2ID = positions.get(1);
        this.place3ID = positions.get(2);
        this.place4ID = positions.get(3);
    }
}
