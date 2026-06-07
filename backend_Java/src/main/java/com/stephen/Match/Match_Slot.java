package com.stephen.Match;

import com.stephen.Frame.Frame_Entity;
import com.stephen.Player.Player_Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "match_slots", uniqueConstraints = @UniqueConstraint(columnNames = {"match_id", "slot_number"}))
public class Match_Slot {

    public enum Status { PENDING, READY, COMPLETE, BYE }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_slot_seq")
    @SequenceGenerator(name = "match_slot_seq", sequenceName = "match_slot_seq", allocationSize = 1)
    private Long Id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match_Entity match;

    @Column(name = "slot_number", nullable = false)
    private int slotNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.PENDING;

    @ManyToOne
    @JoinColumn(name = "player_a_id")
    private Player_Entity playerA;

    @ManyToOne
    @JoinColumn(name = "player_b_id")
    private Player_Entity playerB;

    @ManyToOne
    @JoinColumn(name = "frame_id")
    private Frame_Entity frame;

    protected Match_Slot() {}

    public Match_Slot(Match_Entity match, int slotNumber) {
        this.match = match;
        this.slotNumber = slotNumber;
        this.status = Status.PENDING;
    }

    // called when a player is assigned — auto-transitions to READY if both set
    public void assignPlayerA(Player_Entity player) {
        this.playerA = player;
        updateStatus();
    }

    public void assignPlayerB(Player_Entity player) {
        this.playerB = player;
        updateStatus();
    }

    private void updateStatus() {
        if (status == Status.BYE || status == Status.COMPLETE) return;
        if (playerA != null && playerB != null) {
            status = Status.READY;
        }
    }

    public void linkFrame(Frame_Entity frame) {
        this.frame = frame;
    }

    public void markComplete() {
        this.status = Status.COMPLETE;
    }

    public void markBye() {
        this.status = Status.BYE;
    }

    public boolean isReady() { return status == Status.READY; }
    public boolean isComplete() { return status == Status.COMPLETE; }

    public Long getId() { return Id; }
    public Match_Entity getMatch() { return match; }
    public int getSlotNumber() { return slotNumber; }
    public Status getStatus() { return status; }
    public Player_Entity getPlayerA() { return playerA; }
    public Player_Entity getPlayerB() { return playerB; }
    public Frame_Entity getFrame() { return frame; }
}