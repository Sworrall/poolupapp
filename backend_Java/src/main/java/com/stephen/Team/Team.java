package com.stephen.Team;

import com.stephen.Player.Player;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_seq")
    @SequenceGenerator(name = "team_seq", sequenceName = "team_seq", allocationSize = 1)
    private Long Id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @ManyToOne
    @JoinColumn(name = "captain_id")
    private Player captain;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private Set<Player> players = new HashSet<>();

    @Embedded
    private Team_ContactDetails contactDetails;

    @Column(name = "is_bye", nullable = false)
    private boolean isBye = false;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid
            ;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    protected Team() {}

    // --- FACTORY ---
    public static Team createBye() {
        Team bye = new Team();
        bye.teamName = "BYE";
        bye.isBye = true;
        return bye;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // --- PLAYER MANAGEMENT ---
    public void addPlayer(Player p) {
        Objects.requireNonNull(p, "Player cannot be null");
        if (p.isBye()) throw new IllegalArgumentException("Cannot add bye player to team");
        players.add(p);
        if (captain == null) setCaptain(p);
    }

    public void removePlayer(Player p) {
        Objects.requireNonNull(p, "Player cannot be null");
        players.remove(p);
        if (p.equals(captain)) {
            captain = players.stream().findFirst().orElse(null);
        }
    }

    public void setCaptain(Player p) {
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

    public Player getCaptain() { return captain; }
    public Set<Player> getPlayers() { return players; }

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