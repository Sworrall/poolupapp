package com.stephen.Player;

import com.stephen.BaseStats.BaseStats_Key;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_seq")
    @SequenceGenerator(name = "player_seq", sequenceName = "player_seq", allocationSize = 1)
    private Long Id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "nick_name")
    private String nickName;

    @Embedded
    private Player_ContactDetails contactDetails = new Player_ContactDetails();

    @Column(name = "is_bye", nullable = false)
    private boolean isBye = false;

    @Column(name = "is_captain", nullable = false)
    private boolean isCaptain = false;

    @Column(name = "firebase_uid", unique = true)
    private String firebaseUid;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    protected Player(){}

    // --- FACTORY ---
    public static Player createBye() {
        Player bye = new Player();
        bye.firstName = "BYE";
        bye.lastName = "";
        bye.isBye = true;
        return bye;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // --- DERIVED ---
    public String getFullName() {
        if (isBye) return "BYE";
        String name = (nickName != null && !nickName.isBlank())
                ? firstName + " \"" + nickName + "\" " + lastName
                : firstName + " " + lastName;
        return (isCaptain ? "(C) " : "") + name;
    }

    // --- GETTERS & SETTERS ---
    public Long getId() { return Id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public String getPhoneNumber() { return contactDetails.getPhoneNumber(); }
    public void setPhoneNumber(String phoneNumber) { contactDetails.setPhoneNumber(phoneNumber); }

    public boolean isBye() { return isBye; }

    public boolean isCaptain() { return isCaptain; }
    public void setCaptain(boolean captain) { this.isCaptain = captain; }

    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }

    public Instant getCreatedAt() { return createdAt; }

    public String getName() {
        if (isBye) return "BYE";
        String name = (nickName != null && !nickName.isBlank())
                ? firstName + " \"" + nickName + "\" " + lastName
                : firstName + " " + lastName;
        return (isCaptain ? "(C) " : "") + name;
    }
}