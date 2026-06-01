package com.stephen.Frame;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "frames")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "frame_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Frame {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "frame_seq")
    @SequenceGenerator(name = "frame_seq", sequenceName = "frame_seq", allocationSize = 1)
    private Long ID;

    @Column(name = "is_played", nullable = false)
    private boolean isPlayed = false;

    @Column(name = "is_bye", nullable = false)
    private boolean isBye = false;

    @Column(name = "break_dish", nullable = false)
    private boolean breakDish = false;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    protected Frame() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public Long getID() { return ID; }

    public boolean isPlayed() { return isPlayed; }
    public void setPlayed(boolean played) { this.isPlayed = played; }

    public boolean isBye() { return isBye; }
    public void setBye(boolean bye) { this.isBye = bye; }

    public boolean isBreakDish() { return breakDish; }
    public void setBreakDish(boolean breakDish) { this.breakDish = breakDish; }

    public Instant getCreatedAt() { return createdAt; }
}