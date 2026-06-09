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
public abstract class Tournament_Entity {

    @Transient
    private static final Logger log = LoggerFactory.getLogger(Tournament_Entity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Discriminates which party type participates in this tournament.
     * Used by the service layer to resolve partyIss to the correct repository.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "party_type", nullable = false)
    private PartyType partyType;

    /**
     * Flat list of participant Ids. The service layer resolves these
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
    private List<Long> partyIds = new ArrayList<>();

    // Podium — nullable until tournament is complete.
    // Resolved to full party objects by the service layer via partyType.
    @Column(name = "place1_id")
    private Long place1Id;

    @Column(name = "place2_id")
    private Long place2Id;

    @Column(name = "place3_id")
    private Long place3Id;

    @Column(name = "place4_id")
    private Long place4Id;

    @Column(name = "is_started", nullable = false)
    private boolean isStarted = false;

    @Column(name = "is_complete", nullable = false)
    private boolean isComplete = false;

    // --- JPA ---
    protected Tournament_Entity() {}

    // --- CONSTRUCTOR ---
    protected Tournament_Entity(List<Long> partyIds, PartyType partyType) {
        this.partyIds = new ArrayList<>(partyIds);
        this.partyType = partyType;
    }

    // --- GETTERS ---
    public Long getId() {
        return id;
    }

    public PartyType getPartyType() {
        return partyType;
    }

    public List<Long> getPartyIds() {
        return partyIds;
    }

    public Long getPlace1Id() {
        return place1Id;
    }

    public Long getPlace2Id() {
        return place2Id;
    }

    public Long getPlace3Id() {
        return place3Id;
    }

    public Long getPlace4Id() {
        return place4Id;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isComplete() {
        return isComplete;
    }

    // --- SETTERS ---
    public void setPartyIds(List<Long> partyIds) {
        this.partyIds = new ArrayList<>(partyIds);
    }

    public void setPlace1Id(Long place1Id) {
        this.place1Id = place1Id;
    }

    public void setPlace2Id(Long place2Id) {
        this.place2Id = place2Id;
    }

    public void setPlace3Id(Long place3Id) {
        this.place3Id = place3Id;
    }

    public void setPlace4Id(Long place4Id) {
        this.place4Id = place4Id;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    /**
     * Convenience setter — accepts an ordered list of at least 4 Ids.
     * Consistent with original setPositions() behaviour.
     */
    public void setPositions(List<Long> positions) {
        if (positions.size() < 4) {
            log.error("setPositions requires at least 4 Ids, received {}", positions.size());
            throw new IllegalArgumentException("Need at least 4 positions");
        }
        this.place1Id = positions.get(0);
        this.place2Id = positions.get(1);
        this.place3Id = positions.get(2);
        this.place4Id = positions.get(3);
    }
}
