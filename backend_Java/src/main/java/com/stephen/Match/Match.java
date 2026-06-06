package com.stephen.Match;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "matches")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "match_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_seq")
    @SequenceGenerator(name = "match_seq", sequenceName = "match_seq", allocationSize = 1)
    private Long matchId;

    @Column(name = "frame_count", nullable = false)
    private int frameCount;

    @Column(name = "is_played", nullable = false)
    private boolean isPlayed = false;

    @Column(name = "is_bye", nullable = false)
    private boolean isBye = false;

    @Column(name = "is_draw", nullable = false)
    private boolean isDraw = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    protected Match() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getId() { return matchId; }

    public int getFrameCount() { return frameCount; }
    public void setFrameCount(int frameCount) { this.frameCount = frameCount; }

    public boolean isPlayed() { return isPlayed; }
    public void setPlayed(boolean played) { this.isPlayed = played; }

    public boolean isBye() { return isBye; }
    public void setBye(boolean bye) { this.isBye = bye; }

    public boolean isDraw() { return isDraw; }
    public void setDraw(boolean draw) { this.isDraw = draw; }

    public Instant getCreatedAt() { return createdAt; }
}