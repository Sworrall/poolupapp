package com.stephen.Doubles;

import com.stephen.Player.Player_Entity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "doubles")
public class Doubles_Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doubles_seq")
    @SequenceGenerator(name = "doubles_seq", sequenceName = "doubles_seq", allocationSize = 1)
    private Long Id;

    @Column(name = "team_name")
    private String teamName;

    @ManyToOne
    @JoinColumn(name = "player1_id")
    private Player_Entity player1;

    @ManyToOne
    @JoinColumn(name = "player2_id")
    private Player_Entity player2;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private Player_Entity captain;

    @Embedded
    private Doubles_ContactDetails contactDetails;

    @Column(name = "is_bye", nullable = false)
    private boolean isBye = false;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    protected Doubles_Entity() {}

    // --- FACTORY ---
    public static Doubles_Entity createBye() {
        Doubles_Entity bye = new Doubles_Entity();
        bye.teamName = "BYE";
        bye.isBye = true;
        return bye;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // --- PLAYER MANAGEMENT ---
    public void setPlayers(Player_Entity p1, Player_Entity p2) {
        Objects.requireNonNull(p1, "Player 1 cannot be null");
        Objects.requireNonNull(p2, "Player 2 cannot be null");
        if (p1.isBye() || p2.isBye()) throw new IllegalArgumentException("Cannot add bye player to doubles team");
        if (p1.getId().equals(p2.getId())) throw new IllegalArgumentException("Players must be different");
        this.player1 = p1;
        this.player2 = p2;
    }

    public void setCaptain(Player_Entity captain) {
        if (captain == null || captain.isBye()) throw new IllegalArgumentException("Invalid captain");
        if (!captain.getId().equals(player1.getId()) && !captain.getId().equals(player2.getId())) {
            throw new IllegalArgumentException("Captain must be one of the two players");
        }
        this.captain = captain;
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return Id; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) {
        this.teamName = Objects.requireNonNull(teamName, "Team name cannot be null");
    }

    public Player_Entity getPlayer1() { return player1; }
    public Player_Entity getPlayer2() { return player2; }
    public Player_Entity getCaptain() { return captain; }

    public Doubles_ContactDetails getContactDetails() { return contactDetails; }
    public void setContactDetails(Doubles_ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    public boolean isBye() { return isBye; }

    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }

    public Instant getCreatedAt() { return createdAt; }
}