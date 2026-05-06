package com.stephen.Doubles;

import com.stephen.FireBase.BaseStats_Repository;
import com.stephen.FireBase.Doubles_Repository;
import com.stephen.Functions.ID;
import com.stephen.Player.Player;
import com.stephen.BaseStats.BaseStats;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class Doubles extends ID implements StatHolder<Doubles> {
    private final int GLOBAL = 0;
    private String teamName;
    private Player captain;
    private Doubles_ContactDetails contactDetails;
    private final ArrayList<Player> players;
    private final Map<BaseStats_Key, BaseStats> stats;
    private final boolean isBye;
    private static final Logger log = LoggerFactory.getLogger(Doubles.class);


    // --- CONSTRUCTORS ---
    public Doubles(String teamName) {
        super();
        this.teamName = Objects.requireNonNull(teamName, "Team name cannot be null");
        this.players = new ArrayList<>();
        this.contactDetails = new Doubles_ContactDetails("Somewhere");
        this.isBye = false;
        this.stats = new HashMap<>();
        BaseStats_Key K = new BaseStats_Key(GLOBAL, super.getID());
        stats.computeIfAbsent(K, _ -> new BaseStats());
        updateCloud_All();
    }

    public Doubles() {
        super();
        this.teamName = "Bye";
        this.players = new ArrayList<>();
        this.stats = new HashMap<>();
        this.captain = null;
        this.isBye = true;
        updateCloud_All();
    }


    // --- FIREBASE ---
    public void updateCloud_Attributes(){
        Doubles_Repository doubles_Repository = new Doubles_Repository();
        doubles_Repository.saveDoublesTeam(this);
        log.info("Updated cloud attributes for doubles team {}", teamName);
    }

    public void updateCloud_Stats() {
        BaseStats_Repository<Doubles> baseStatRepo = new BaseStats_Repository<>();
        baseStatRepo.saveStats(this);
        log.info("Updated cloud stats for doubles team {}", teamName);
    }

    public void updateCloud_All() {
        this.updateCloud_Attributes();
        this.updateCloud_Stats();
        log.info("Updated Cloud for doubles team {}", teamName);
    }


    // --- FACTORY ---
    public static Doubles createBye(){
        return new Doubles();
    }


    // --- GETTERS ---
    @Override
    public String getName() {
        return this.teamName;
    }

    @Override
    public boolean isBye() {
        return isBye;
    }

    @Override
    public BaseStats getOrCreateStats(BaseStats_Key K) {
        return getOrCreateTeamStats(K);
    }

    @Override
    public Doubles createByeParty() {
        return new Doubles();
    }

    public Map<BaseStats_Key, BaseStats> getStatsMap() {
        return this.stats;
    }


    // -- GETTERS ---
    public String getDoublesName() {
        return teamName;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    public Player getPlayer(int id) {
        if(players.getFirst().getID() == id){
            return players.getFirst();
        }else if (players.getLast().getID() == id){
            return players.getLast();
        }
        else{
            log.error("getPlayer() Player with ID {} not found in team {}", id, teamName);
            throw new IllegalArgumentException("Player not Found");
        }
    }

    public Player getCaptain() {
        return captain;
    }


    // --- SETTERS ---
    public void setDoublesName(String teamName) {
        this.teamName = Objects.requireNonNull(teamName, "Team name cannot be null");
        this.updateCloud_Attributes();
    }

    public void setHomePhoneNumber(int homeNumber){
        this.contactDetails.setHomePhoneNumber(homeNumber);
        this.updateCloud_Attributes();
    }


    // --- STATS ---
    public BaseStats getOrCreateTeamStats(BaseStats_Key K) {
        log.info("Getting or creating team stats for team {} with key: {}", teamName, K);
        return this.stats.computeIfAbsent(K, _ -> new BaseStats());
    }


    // --- UPDATE ---
    public void updateHomeLocation(int homeNumber, String address){
        log.info("Updating home location for team {} to {}", teamName, address);
        this.contactDetails.updateHomeLocation(homeNumber, address);
    }


    // --- PLAYER MANAGEMENT ---
    public void addPlayer(Player p) {
        if (p == null) {
            log.error("addPlayer() Player cannot be null");
        }else if(players.size() >= 2){
            log.error("addPlayer() Team already has 2 players");
        }else if (p.isBye()) {
            log.error("addPlayer() Cannot add Bye player to team");
        }else if (players.contains(p)) {
            log.error("addPlayer() Player already in team");
        }else{
            players.add(p);
            if (captain == null) {
                updateCaptain(p);
            }
            p.getOrCreateStats( new BaseStats_Key(GLOBAL, super.getID()));
            this.updateCloud_All();
            log.info("Added player {} to team {}", p.getName(), teamName);
        }

    }

    public void removePlayer(int id) {
        Player toRemove = players.stream()
                .filter(p -> p.getID() == id)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("removePlayer() Player with ID {} not found in team {}", id, teamName);
                    return new IllegalArgumentException("Player not Found");
                });
        players.remove(toRemove);
        if (toRemove.equals(captain)) {
            toRemove.removeCaptain();
            captain = null;
            if (!players.isEmpty()) {
                updateCaptain(players.getFirst());
            }
        }
        this.updateCloud_Attributes();
        log.info("Removed player {} from team {}", toRemove.getName(), teamName);
    }

    public void updateCaptain(Player newCaptain) {
        if (newCaptain.isBye()) {
            log.warn("updateCaptain() Bye player cannot be captain");
        }else if (!players.contains(newCaptain)) {
            log.warn("updateCaptain() Captain must be in the team");
        }else{
            captain = newCaptain;
            captain.makeCaptain();
            log.info("Updated captain for team {} to {}", teamName, newCaptain.getName());
        }
        this.updateCloud_Attributes();
    }
}
