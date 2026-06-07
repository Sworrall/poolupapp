package com.stephen.Team;

import com.stephen.Player.Player_Entity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team_Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_seq")
    @SequenceGenerator(name = "team_seq", sequenceName = "team_seq", allocationSize = 1)
    private Long Id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private Player_Entity captain;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player_Entity> players = new HashSet<>();

    @Embedded
    private Team_ContactDetails contactDetails;

    @Column(name = "is_bye", nullable = false)
    private boolean isBye = false;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid
            ;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    protected Team_Entity() {}

    // --- FACTORY ---
    public static Team_Entity createBye() {
        Team_Entity bye = new Team_Entity();
        bye.teamName = "BYE";
        bye.isBye = true;
        return bye;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // --- PLAYER MANAGEMENT ---
    public void addPlayer(Player_Entity p) {
        Objects.requireNonNull(p, "Player cannot be null");
        if (p.isBye()) throw new IllegalArgumentException("Cannot add bye player to team");
        players.add(p);
        if (captain == null) setCaptain(p);
    }

    public void removePlayer(Player_Entity p) {
        Objects.requireNonNull(p, "Player cannot be null");
        players.remove(p);
        if (p.equals(captain)) {
            captain = players.stream().findFirst().orElse(null);
        }
    }

    public void setCaptain(Player_Entity p) {
        Objects.requireNonNull(p, "Captain cannot be null");
        if (p.isBye()) throw new IllegalArgumentException("Bye player cannot be captain");
        if (!players.contains(p)) throw new IllegalArgumentException("Captain must be in the team");
        this.captain = p;
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return Id; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) {
        this.teamName = Objects.requireNonNull(teamName, "Team name cannot be null");
    }

    public Player_Entity getCaptain() { return captain; }
    public Set<Player_Entity> getPlayers() { return players; }

    public Team_ContactDetails getContactDetails() { return contactDetails; }
    public void setContactDetails(Team_ContactDetails contactDetails) {
        this.contactDetails = contactDetails;
    }

    public boolean isBye() { return isBye; }

    public String getFirebaseUid() {
        return firebaseUid;
    }

    public void setFirebaseUid(String firebaseUid) {
        this.firebaseUid = firebaseUid;
    }

    public Instant getCreatedAt() { return createdAt; }
}