package com.stephen.BaseStats;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Persistent stat record for one party in one event context.
 *
 * One row per (holderID, holderType, eventID, eventScope, teamID) combination:
 *
 *   eventScope = GLOBAL     → career stats, eventID = 0
 *   eventScope = FRAME      → stats for one specific frame
 *   eventScope = MATCH      → stats for one specific match
 *   eventScope = TOURNAMENT → stats for one specific tournament
 *
 * matchID and tournamentID carry parent context on frame/match rows so stats
 * can be traced back up the hierarchy without joins:
 *
 *   Frame row:      (frameID,      FRAME,      matchID,  tournamentID)
 *   Match row:      (matchID,      MATCH,      null,     tournamentID)
 *   Tournament row: (tournamentID, TOURNAMENT, null,     null)
 *   Global row:     (0,            GLOBAL,     null,     null)
 */
@Entity
@Table(name = "stat_entry", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_stat_entry",
                columnNames = {"holder_id", "holder_type", "event_id", "event_scope", "team_id"}
        )
}, indexes = {
        @Index(name = "idx_stat_holder",     columnList = "holder_id, holder_type"),
        @Index(name = "idx_stat_tournament", columnList = "tournament_id"),
        @Index(name = "idx_stat_match",      columnList = "match_id")
})
public class StatEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stat_entry_seq")
    @SequenceGenerator(name = "stat_entry_seq", sequenceName = "stat_entry_seq", allocationSize = 1)
    private Long ID;

    @Column(name = "holder_id", nullable = false)
    private Long holderID;

    @Enumerated(EnumType.STRING)
    @Column(name = "holder_type", nullable = false)
    private HolderType holderType;

    /**
     * The ID of the event this row is scoped to.
     * 0 = global. Otherwise, a frame, match, or tournament ID per eventScope.
     */
    @Column(name = "event_id", nullable = false)
    private Long eventID;

    /**
     * What kind of event event_id refers to.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "event_scope", nullable = false)
    private StatScope eventScope;

    /**
     * Parent match ID. Set on FRAME-scoped rows only.
     * Null on MATCH, TOURNAMENT, and GLOBAL rows.
     */
    @Column(name = "match_id")
    private Long matchID;

    /**
     * Parent tournament ID. Set on FRAME and MATCH-scoped rows where
     * the match belongs to a tournament. Null for standalone matches,
     * TOURNAMENT rows, and GLOBAL rows.
     */
    @Column(name = "tournament_id")
    private Long tournamentID;

    /**
     * Optional team context. Null for standalone singles/doubles events.
     */
    @Column(name = "team_id")
    private Long teamID;

    // --- STAT FIELDS ---
    @Column(name = "frame_win",       nullable = false) private int frameWin      = 0;
    @Column(name = "frame_loss",      nullable = false) private int frameLoss     = 0;
    @Column(name = "frame_break_dish",nullable = false) private int frameBreakDish= 0;
    @Column(name = "frame_total",     nullable = false) private int frameTotal    = 0;
    @Column(name = "match_win",       nullable = false) private int matchWin      = 0;
    @Column(name = "match_loss",      nullable = false) private int matchLoss     = 0;
    @Column(name = "match_draw",      nullable = false) private int matchDraw     = 0;
    @Column(name = "match_total",     nullable = false) private int matchTotal    = 0;

    @Column(name = "created_at", updatable = false) private Instant createdAt;
    @Column(name = "updated_at")                    private Instant updatedAt;

    // --- JPA ---
    protected StatEntry() {}

    // --- CONSTRUCTOR ---
    public StatEntry(Long holderID, HolderType holderType,
                     Long eventID, StatScope eventScope,
                     Long matchID, Long tournamentID, Long teamID) {
        this.holderID     = holderID;
        this.holderType   = holderType;
        this.eventID      = eventID;
        this.eventScope   = eventScope;
        this.matchID      = matchID;
        this.tournamentID = tournamentID;
        this.teamID       = teamID;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // --- CORE API ---
    public void increment(StatField field)        { set(field, get(field) + 1); }
    public void add(StatField field, int value)   { set(field, get(field) + value); }

    public int get(StatField field) {
        return switch (field) {
            case FRAME_WIN        -> frameWin;
            case FRAME_LOSS       -> frameLoss;
            case FRAME_BREAK_DISH -> frameBreakDish;
            case FRAME_TOTAL      -> frameTotal;
            case MATCH_WIN        -> matchWin;
            case MATCH_LOSS       -> matchLoss;
            case MATCH_DRAW       -> matchDraw;
            case MATCH_TOTAL      -> matchTotal;
        };
    }

    public void set(StatField field, int value) {
        switch (field) {
            case FRAME_WIN        -> frameWin        = value;
            case FRAME_LOSS       -> frameLoss       = value;
            case FRAME_BREAK_DISH -> frameBreakDish  = value;
            case FRAME_TOTAL      -> frameTotal      = value;
            case MATCH_WIN        -> matchWin        = value;
            case MATCH_LOSS       -> matchLoss       = value;
            case MATCH_DRAW       -> matchDraw       = value;
            case MATCH_TOTAL      -> matchTotal      = value;
        }
    }

    // --- GETTERS ---
    public Long getID()               { return ID; }
    public Long getHolderID()         { return holderID; }
    public HolderType getHolderType() { return holderType; }
    public Long getEventID()          { return eventID; }
    public StatScope getEventScope()  { return eventScope; }
    public Long getMatchID()          { return matchID; }
    public Long getTournamentID()     { return tournamentID; }
    public Long getTeamID()           { return teamID; }
    public int getFrameWin()          { return frameWin; }
    public int getFrameLoss()         { return frameLoss; }
    public int getFrameBreakDish()    { return frameBreakDish; }
    public int getFrameTotal()        { return frameTotal; }
    public int getMatchWin()          { return matchWin; }
    public int getMatchLoss()         { return matchLoss; }
    public int getMatchDraw()         { return matchDraw; }
    public int getMatchTotal()        { return matchTotal; }
    public Instant getCreatedAt()     { return createdAt; }
    public Instant getUpdatedAt()     { return updatedAt; }
}
