package com.stephen.Player;

import java.util.*;
import com.stephen.FireBase.BaseStats_Repository;
import com.stephen.FireBase.Player_Repository;
import com.stephen.Functions.ID;
import com.stephen.BaseStats.BaseStats;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class Player extends ID implements StatHolder<Player> {
    private static final int GLOBAL = 0;
    private String firstName;
    private String lastName;
    private String nickName;
    private final Player_ContactDetails contactDetails;
    private final boolean isBye;
    private boolean isCaptain;
    private final Map<BaseStats_Key, BaseStats> stats;
    private static final Logger log = LoggerFactory.getLogger(Player.class);


    // --- CONSTRUCTORS ---
    public Player(String firstName, String lastName) {
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.nickName = null;
        this.contactDetails = new Player_ContactDetails();
        this.isBye = false;
        this.isCaptain = false;
        this.stats = new HashMap<>();
        this.stats.put(new BaseStats_Key(GLOBAL, null), new BaseStats());
        updateCloud_All();
        log.info("Created player: {}", getFullName());
    }

    public Player() {
        super();
        this.firstName = "BYE";
        this.lastName = "";
        this.nickName = null;
        this.contactDetails = new Player_ContactDetails();
        this.isBye = true;
        this.isCaptain = false;
        this.stats = new HashMap<>();
        log.info("Created bye player");
    }


    // --- FIREBASE ---
    public void updateCloud_Attributes() {
        Player_Repository playerRepo = new Player_Repository();
        playerRepo.savePlayer(this);
        log.info("Updated cloud attributes for player {}", getFullName());
    }

    public void updateCloud_Stats() {
        BaseStats_Repository<Player> baseStatRepo = new BaseStats_Repository<>();
        baseStatRepo.saveStats(this);
        log.info("Updated cloud stats for player {}", getFullName());
    }

    public void updateCloud_All() {
        updateCloud_Attributes();
        updateCloud_Stats();
        log.info("Updated cloud player {}", getFullName());
    }

    // --- FACTORY ---
    public static Player createBye(){
        return new Player();
    }

    public Player createByeParty(){
        return new Player();
    }

    public Map<BaseStats_Key, BaseStats> getStatsMap() {
        return this.stats;
    }


    // --- INTERFACE ---
    @Override
    public String getName() {
        return getFullName();
    }

    @Override
    public boolean isBye(){
        return isBye;
    }

    @Override
    public BaseStats getOrCreateStats(BaseStats_Key K) {
        log.info("Getting or creating stats for player: {} - Key: {}", getFullName(), K);
        return stats.computeIfAbsent(K, _ -> new BaseStats());
    }


    // --- GETTERS ---
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getNickName() {
        return (nickName == null || nickName.isBlank()) ? "" : nickName;
    }

    public boolean isCaptain() {
        return isCaptain;
    }

    public String getFullName() {
        if (isBye) return "BYE";
        String name = (nickName != null && !nickName.isBlank())
                ? firstName + " \"" + nickName + "\" " + lastName
                : firstName + " " + lastName;
        return (isCaptain ? "(C) " : "") + name;
    }

    public Player_ContactDetails getContactDetails(){
        return this.contactDetails;
    }


    // --- SETTERS ---
    public void setName(String firstName, String lastName, String nickName) {
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.nickName = nickName;
        this.updateCloud_Attributes();
    }

    public void makeCaptain() {
        this.isCaptain = true;
        this.updateCloud_Attributes();
    }

    public void removeCaptain() {
        this.isCaptain = false;
        this.updateCloud_Attributes();
    }

    public void setMobileNumber(int number) {
        contactDetails.setPhoneNumber(number);
        this.updateCloud_Attributes();
    }
}