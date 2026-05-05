package com.stephen.FireBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stephen.BaseStats.BaseStats;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.StatField;
import com.stephen.Functions.DataCallback;
import com.stephen.Player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Player_Repository {

    private static final Logger log = LoggerFactory.getLogger(Player_Repository.class);
    private final Firestore db;

    // --- CONSTRUCTOR ---
    public Player_Repository() {
        this.db = FirestoreClient.getFirestore();
        log.info("PlayerRepository initialised");
    }

    // --- SAVE ---
    public void savePlayer(Player player) {
        try {
            log.info("Attempting to save player: {}", player.getFullName());
            Map<String, Object> data = new HashMap<>();
            data.put("firstName", player.getFirstName());
            data.put("lastName", player.getLastName());
            data.put("nickName", player.getNickName());
            data.put("isBye", player.isBye());
            data.put("isCaptain", player.isCaptain());
            log.info("Data map built: {}", data);
            db.collection("Player")
                    .document(String.valueOf(player.getID()))
                    .set(data)
                    .get();
            log.info("Firestore write confirmed for player: {}", player.getFullName());
        } catch (Exception e) {
            log.error("Failed to save player: {}", player.getFullName(), e);
        }
    }

    public void getPlayer(String playerID, DataCallback<Player> callback) {
        log.info("Fetching player with ID: {}", playerID);
        // Use a listener to avoid blocking the main mobile thread
        db.collection("Player").document(playerID).get().addListener(() -> {
            try {
                // .get().get() resolves the future and gives us the actual data
                var document = db.collection("Player").document(playerID).get().get();
                if (document.exists()) {

                    // 1. Pull the raw data from Firestore
                    String first = document.getString("firstName");
                    String last = document.getString("lastName");
                    String nick = document.getString("nickName");
                    Boolean isBye = document.getBoolean("isBye");
                    Boolean isCap = document.getBoolean("isCaptain");

                    // 2. Reconstruct your Player object
                    // (Assumes a constructor exists or use setters)
                    Player p = new Player(first, last);
                    p.setName(first, last, nick);
                    // Ensure you handle nulls if fields don't exist in DB
                    if (isCap != null) p.makeCaptain();

                    // 3. Send the completed object back to the UI
                    callback.onSuccess(p);
                    log.info("Player {} retrieved successfully", p.getFullName());
                } else {
                    callback.onError(new Exception("Player ID " + playerID + " not found."));
                }
            } catch (Exception e) {
                log.error("Error retrieving player: {}", playerID, e);
                callback.onError(e);
            }
        }, Runnable::run);
    }

    public void verifyPlayer(Player existing, DataCallback<Player> callback) {
        log.info("Verifying player: {} (ID: {})", existing.getFullName(), existing.getID());
        getPlayer(String.valueOf(existing.getID()), new DataCallback<>() {

            @Override
            public void onSuccess(Player retrieved) {

                // --- BASIC FIELDS ---
                boolean firstNameMatch  = Objects.equals(existing.getFirstName(), retrieved.getFirstName());
                boolean lastNameMatch   = Objects.equals(existing.getLastName(),  retrieved.getLastName());
                boolean nickNameMatch   = Objects.equals(existing.getNickName(),  retrieved.getNickName());
                boolean captainMatch    = existing.isCaptain() == retrieved.isCaptain();

                log.info("=== PLAYER VERIFICATION: {} ===", existing.getFullName());
                log.info("  firstName  : {} | {} | {}", existing.getFirstName(), retrieved.getFirstName(), firstNameMatch  ? "OK" : "MISMATCH");
                log.info("  lastName   : {} | {} | {}", existing.getLastName(),  retrieved.getLastName(),  lastNameMatch   ? "OK" : "MISMATCH");
                log.info("  nickName   : {} | {} | {}", existing.getNickName(),  retrieved.getNickName(),  nickNameMatch   ? "OK" : "MISMATCH");
                log.info("  isCaptain  : {} | {} | {}", existing.isCaptain(),    retrieved.isCaptain(),    captainMatch    ? "OK" : "MISMATCH");

                // --- STATS ---
                Map<BaseStats_Key, BaseStats> existingStats  = existing.getStatsMap();
                Map<BaseStats_Key, BaseStats> retrievedStats = retrieved.getStatsMap();

                log.info("  stat entries (existing) : {}", existingStats.size());
                log.info("  stat entries (retrieved): {}", retrievedStats.size());

                boolean allStatsMatch = true;
                for (Map.Entry<BaseStats_Key, BaseStats> entry : existingStats.entrySet()) {
                    BaseStats_Key key = entry.getKey();
                    BaseStats existingStat = entry.getValue();
                    BaseStats retrievedStat = retrievedStats.get(key);

                    if (retrievedStat == null) {
                        log.warn("  MISSING key in retrieved: eventID={}, teamID={}", key.eventID(), key.teamID());
                        allStatsMatch = false;
                        continue;
                    }

                    for (StatField field : StatField.values()) {
                        int e = existingStat.get(field);
                        int r = retrievedStat.get(field);
                        if (e != r) {
                            log.warn("  MISMATCH key=({},{}) field={} | existing={} retrieved={}",
                                    key.eventID(), key.teamID(), field, e, r);
                            allStatsMatch = false;
                        }
                    }
                }

                // catch any keys in retrieved that weren't in existing
                for (BaseStats_Key key : retrievedStats.keySet()) {
                    if (!existingStats.containsKey(key)) {
                        log.warn("  EXTRA key in retrieved (not in existing): eventID={}, teamID={}", key.eventID(), key.teamID());
                        allStatsMatch = false;
                    }
                }

                if (allStatsMatch) {
                    log.info("  ALL STATS MATCH");
                }
                log.info("=== END VERIFICATION: {} ===", existing.getFullName());
                callback.onSuccess(retrieved);
            }

            @Override
            public void onError(Exception e) {
                log.error("Verification failed - could not retrieve player ID: {}", existing.getID(), e);
                callback.onError(e);
            }
        });
    }
}