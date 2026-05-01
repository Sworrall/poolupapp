package com.stephen;

import java.util.*;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Team extends ID implements StatHolder<Team> {
    private final int GLOBAL = 0;
    private String teamName;
    private Player captain;
    private Team_ContactDetails contactDetails;
    private final ArrayList<Player> players;
    private final Map<BaseStats_Key, BaseStats> stats;
    private final boolean isBye;
    private static final Logger log = LoggerFactory.getLogger(Team.class);


    // --- CONSTRUCTORS ---
    public Team(String teamName) {
        super();
        this.teamName = Objects.requireNonNull(teamName, "Team name cannot be null");
        this.players = new ArrayList<>();
        this.stats = new HashMap<>();
        this.contactDetails = new Team_ContactDetails();
        this.isBye = false;
        this.getOrCreateTeamStats(new BaseStats_Key(GLOBAL, super.getID()));
        log.info("Created team: {}", teamName);
    }
    
    public Team() {
        this.teamName = "Bye Team";
        this.players = new ArrayList<>();
        this.stats = new HashMap<>();
        this.captain = null;
        this.isBye = true;
        log.info("Created bye team");
    }


    // --- FACTORY ---
    public static Team createBye(){
        log.info("Creating bye team");
        return new Team();
    }

    public Team createByeParty() {
        log.info("Creating bye party");
        return new Team();
    }

    public boolean isBye() {
        log.info("Checking if team is bye: {}", teamName);
        return isBye;
    }


    // --- INTERFACE ---
    @Override
    public String getName() {
        log.info("Getting team name: {}", teamName);
        return this.teamName;
    }

    @Override
    public BaseStats getOrCreateStats(BaseStats_Key K) {
        log.info("Getting or creating stats for team: {} with key: {}", teamName, K);
        return getOrCreateTeamStats(K);
    }


    // --- STATS ---
    public BaseStats getOrCreateTeamStats(BaseStats_Key K) {
        log.info("Getting or creating team stats for: {} with key: {}", teamName, K);
        return this.stats.computeIfAbsent(K, _ -> new BaseStats());
    }


    // --- GETTERS ---
    public String getTeamName() {
        log.info("Getting team name: {}", teamName);
        return teamName;
    }

    public ArrayList<Player> getPlayers() {
        log.info("Getting players for team: {}", teamName);
        return players;
    }

    public Player getPlayer(int id) {
        log.info("Getting player with ID: {} for team: {}", id, teamName);
        return players.stream()
                .filter(p -> p.getID() == id)
                .findFirst()
                .orElseThrow(() -> {
                    log.info("player not found");
                    return new IllegalArgumentException("Player not found");
                });

    }

    public Player getCaptain() {
        log.info("Getting captain for team: {}", teamName);
        return captain;
    }


    // --- SETTERS ---
    public void setTeamName(String teamName) {
        this.teamName = Objects.requireNonNull(teamName, "Team name cannot be null");
        log.info("Setting team name: {}", teamName);
    }

    public void setHomePhoneNumber(int homeNumber){
        this.contactDetails.setHomePhoneNumber(homeNumber);
        log.info("Setting home phone number for team: {} to: {}", teamName, homeNumber);
    }


    // --- UPDATE ---
    public void updateHomeLocation(int homeNumber, String address){
        this.contactDetails.updateHomeLocation(homeNumber, address);
        log.info("Updating home location for team: {} to: {}", teamName, address);
    }


    // --- PLAYER MANAGEMENT ---
    public void addPlayer(Player p) {
        if (p == null) {
            log.info("Player cannot be null");
        }else if (p.isBye()) {
            log.info("Cannot add Bye player to team");
        }else if (players.contains(p)) {
            log.info("Player already in team");
        }else if (captain == null) {
            updateCaptain(p);
        }
        players.add(p);
        assert p != null;
        p.getOrCreateStats(new BaseStats_Key(GLOBAL, super.getID()));
        log.info("Added player: {} to team: {}", p.getName(), teamName);
    }

    public void removePlayer(int id) {
        Player toRemove = players.stream()
                .filter(p -> p.getID() == id)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Player not found");
                    return new IllegalArgumentException("Player not found");
                });
        players.remove(toRemove);
        if (toRemove.equals(captain)) {
            toRemove.removeCaptain();
            captain = null;
            if (!players.isEmpty()) {
                updateCaptain(players.getFirst()); // assign new captain safely
            }
        }
        log.info("Removed player with ID: {} from team: {}", id, teamName);
    }

    public void updateCaptain(Player newCaptain) {
        if (newCaptain == null) {
            log.error("Captain cannot be null");
        }else if (newCaptain.isBye()) {
            log.error("Bye player cannot be captain");
        }else if (!players.contains(newCaptain)) {
            log.error("Captain must be in the team");
        }
        captain = newCaptain;
        assert captain != null;
        captain.makeCaptain();
        log.info("Updated captain for team: {} to: {}", teamName, newCaptain.getName());
    }
}