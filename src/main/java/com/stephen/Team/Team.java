package com.stephen.Team;

import java.util.*;
import com.stephen.FireBase.BaseStats_Repository;
import com.stephen.FireBase.Team_Repository;
import com.stephen.Functions.ID;
import com.stephen.Player.Player;
import com.stephen.BaseStats.BaseStats;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.StatHolder;
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
        updateCloud_All();
    }
    
    public Team() {
        this.teamName = "Bye Team";
        this.players = new ArrayList<>();
        this.stats = new HashMap<>();
        this.captain = null;
        this.isBye = true;
        updateCloud_All();
    }


    // --- FIREBASE ---
    public void updateCloud_Attributes(){
        Team_Repository teamRepository = new Team_Repository();
        teamRepository.saveTeam(this);
        log.info("Updated cloud attributes for team {}", teamName);
    }

    public void updateCloud_Stats() {
        BaseStats_Repository<Team> baseStatRepo = new BaseStats_Repository<>();
        baseStatRepo.saveStats(this);
        log.info("Updated cloud stats for team {}", teamName);

    }

    public void updateCloud_All() {
        this.updateCloud_Attributes();
        this.updateCloud_Stats();
        log.info("Updated cloud team {}", teamName);
    }


    // --- FACTORY ---
    public static Team createBye(){
        return new Team();
    }

    public Team createByeParty() {
        return new Team();
    }

    public boolean isBye() {
        return isBye;
    }


    // --- INTERFACE ---
    @Override
    public String getName() {
        return this.teamName;
    }

    @Override
    public BaseStats getOrCreateStats(BaseStats_Key K) {
        return getOrCreateTeamStats(K);
    }

    public Map<BaseStats_Key, BaseStats> getStatsMap() {
        return this.stats;
    }


    // --- STATS ---
    public BaseStats getOrCreateTeamStats(BaseStats_Key K) {
        return this.stats.computeIfAbsent(K, _ -> new BaseStats());
    }


    // --- GETTERS ---
    public String getTeamName() {
        return teamName;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(int id) {
        return players.stream()
                .filter(p -> p.getID() == id)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("player not found");
                    return new IllegalArgumentException("Player not found");
                });

    }

    public Player getCaptain() {
        return captain;
    }


    // --- SETTERS ---
    public void setTeamName(String teamName) {
        this.teamName = Objects.requireNonNull(teamName, "Team name cannot be null");
        this.updateCloud_Attributes();
    }

    public void setHomePhoneNumber(int homeNumber){
        this.contactDetails.setHomePhoneNumber(homeNumber);
        this.updateCloud_Attributes();
    }


    // --- UPDATE ---
    public void updateHomeLocation(int homeNumber, String address){
        this.contactDetails.updateContactDetails(homeNumber, address);
        this.updateCloud_Attributes();
    }


    // --- PLAYER MANAGEMENT ---
    public void addPlayer(Player p) {
        if (p == null) {
            log.warn("Player cannot be null");
        } else if (p.isBye()) {
            log.warn("Cannot add Bye player to team");
        } else if (players.contains(p)) {
            log.warn("Player already in team");
        } else {
            players.add(p);
            if (captain == null) {
                updateCaptain(p);
            }
            p.getOrCreateStats(new BaseStats_Key(GLOBAL, super.getID()));
            log.info("Added player: {} to team: {}", p.getName(), teamName);
        }
        this.updateCloud_All();
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
        this.updateCloud_Attributes();
        log.info("Removed player: {} from team: {}", toRemove.getFullName(), teamName);
    }

    public void updateCaptain(Player newCaptain) {
        if (newCaptain == null) {
            log.warn("Captain cannot be null");
        }else if (newCaptain.isBye()) {
            log.warn("Bye player cannot be captain");
        }else if (!players.contains(newCaptain)) {
            log.warn("Captain must be in the team");
        }else{
            captain = newCaptain;
            captain.makeCaptain();
            log.info("Updated captain for team: {} to: {}", teamName, newCaptain.getName());
        }
        this.updateCloud_All();
    }
}