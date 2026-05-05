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
    public void updateCloud_StatHolder(){
        Player_Repository playerRepo = new Player_Repository();
        playerRepo.savePlayer(this);
    }

    public void updateCloud_Stats() {
        BaseStats_Repository<Player> baseStatRepo = new BaseStats_Repository<>();
        baseStatRepo.saveStats(this);
    }

    public void updateCloud_All() {
        this.updateCloud_StatHolder();
        this.updateCloud_Stats();
    }

    // --- FACTORY ---
    public static Player createBye(){
        log.info("Creating bye player");
        return new Player();
    }

    public Player createByeParty(){
        log.info("Creating bye party");
        return new Player();
    }

    public Map<BaseStats_Key, BaseStats> getStatsMap() {
        return this.stats;
    }


    // --- INTERFACE ---
    @Override
    public String getName() {
        log.info("Getting name for player: {}", getFullName());
        return getFullName();
    }

    @Override
    public boolean isBye(){
        log.info("Checking if player is bye: {} - {}", getFullName(), isBye);
        return isBye;
    }

    @Override
    public BaseStats getOrCreateStats(BaseStats_Key K) {
        log.info("Getting or creating stats for player: {} - Key: {}", getFullName(), K);
        return stats.computeIfAbsent(K, _ -> new BaseStats());
    }


    // --- GETTERS ---
    public String getFirstName() {
        log.info("Getting first name for player: {}", getFullName());
        return firstName;
    }

    public String getLastName() {
        log.info("Getting last name for player: {}", getFullName());
        return lastName;
    }

    public String getNickName() {
        log.info("Getting nickname for player: {}", getFullName());
        return (nickName == null || nickName.isBlank()) ? "" : nickName;
    }

    public boolean isCaptain() {
        log.info("Checking if player is captain: {} - {}", getFullName(), isCaptain);
        return isCaptain;
    }

    public String getFullName() {
        if (isBye) return "BYE";
        String name = (nickName != null && !nickName.isBlank())
                ? firstName + " \"" + nickName + "\" " + lastName
                : firstName + " " + lastName;
        log.info("Getting full name for playerID: {}, player: {}", this.getID(), name);
        return (isCaptain ? "(C) " : "") + name;
    }

    public Player_ContactDetails getContactDetails(){
        log.info("Getting contact details for player: {}", getFullName());
        return this.contactDetails;
    }


    // --- SETTERS ---
    public void setName(String firstName, String lastName, String nickName) {
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        updateNickName(nickName);
        log.info("Set name for player: {}", getFullName());
    }

    public void makeCaptain() {
        this.isCaptain = true;
        log.info("Made player captain: {}", getFullName());
    }

    public void removeCaptain() {
        this.isCaptain = false;
        log.info("Removed captain status from player: {}", getFullName());
    }

    public void updateNickName(String nickName) {
        this.nickName = nickName;
        log.info("Updated nickname for player: {} - New Nickname: {}", getFullName(), this.nickName);
    }

    public void setMobileNumber(int number) {
        contactDetails.setPhoneNumber(number);
        log.info("Set mobile number for player: {} - New Mobile Number: {}", getFullName(), number);
    }
}