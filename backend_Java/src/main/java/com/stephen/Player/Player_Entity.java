package com.stephen.Player;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "players")
public class Player_Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_seq")
    @SequenceGenerator(name = "player_seq", sequenceName = "player_seq", allocationSize = 1)
    private Long ID;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "phone_number")
    private Long phoneNumber;

    @Column(name = "is_bye", nullable = false)
    private boolean isBye = false;

    @Column(name = "is_captain", nullable = false)
    private boolean isCaptain = false;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUID; // links this player to their Firebase auth account

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // --- Getters & Setters ---
    public Long getID() { return ID; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public Long getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(Long phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isBye() { return isBye; }
    public void setBye(boolean bye) { isBye = bye; }

    public boolean isCaptain() { return isCaptain; }
    public void setCaptain(boolean captain) { isCaptain = captain; }

    public String getFirebaseUID() { return firebaseUID; }
    public void setFirebaseUID(String firebaseUID) { this.firebaseUID = firebaseUID; }

    public Instant getCreatedAt() { return createdAt; }
}