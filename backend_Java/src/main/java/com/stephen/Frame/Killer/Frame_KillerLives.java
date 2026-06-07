package com.stephen.Frame.Killer;

import com.stephen.Player.Player_Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "killer_player_lives")
public class Frame_KillerLives {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "killer_lives_seq")
    @SequenceGenerator(name = "killer_lives_seq", sequenceName = "killer_lives_seq", allocationSize = 1)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "frame_id", nullable = false)
    private Frame_Killer frame;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private Player_Entity player;

    @Column(name = "lives_remaining", nullable = false)
    private int livesRemaining;

    protected Frame_KillerLives() {}

    public Frame_KillerLives(Frame_Killer frame, Player_Entity player, int startingLives) {
        this.frame = frame;
        this.player = player;
        this.livesRemaining = startingLives;
    }

    public Long getId() { return Id; }
    public Frame_Killer getFrame() { return frame; }
    public Player_Entity getPlayer() { return player; }
    public int getLivesRemaining() { return livesRemaining; }
    public void setLivesRemaining(int lives) { this.livesRemaining = lives; }
}