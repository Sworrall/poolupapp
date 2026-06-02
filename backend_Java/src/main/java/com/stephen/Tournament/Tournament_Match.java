package com.stephen.Tournament;

import com.stephen.Match.Match;
import jakarta.persistence.*;

/**
 * Join entity between Tournament and Match.

 * Replaces the original ArrayList<ArrayList<Match<S>>> matchList structure.
 * The outer dimension semantics (round vs group) differed per subtype in the
 * original code; roundNumber captures that uniformly:

 *   Tournament_KO         → roundNumber = KO round index (0 = first round)
 *   Tournament_GroupStage → roundNumber = group index
 *   Tournament_RoundRobin → roundNumber = 0 (all fixtures in one pool)
 *   Tournament_Killer     → roundNumber = 0 (all fixtures in one pool)

 * sequence preserves ordering within a round/group, which matters for
 * KO bracket display and deterministic reconstruction of the original lists.
 */
@Entity
@Table(name = "tournament_match", indexes = {
        @Index(name = "idx_tm_tournament", columnList = "tournament_id"),
        @Index(name = "idx_tm_match", columnList = "match_id")
})
public class Tournament_Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    /**
     * Round index (KO) or group index (GroupStage).
     * Always 0 for RoundRobin and Killer.
     */
    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    /**
     * Position within the round/group. Preserves fixture order.
     */
    @Column(name = "sequence", nullable = false)
    private int sequence;

    // --- JPA ---
    protected Tournament_Match() {}

    // --- CONSTRUCTOR ---
    public Tournament_Match(Tournament tournament, Match match, int roundNumber, int sequence) {
        this.tournament = tournament;
        this.match = match;
        this.roundNumber = roundNumber;
        this.sequence = sequence;
    }

    // --- GETTERS ---
    public Long getId() {
        return Id;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public Match getMatch() {
        return match;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getSequence() {
        return sequence;
    }

    // --- SETTERS ---
    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
