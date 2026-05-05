package com.stephen.FireBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stephen.BaseStats.BaseStats;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.StatField;
import com.stephen.Player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Player_Repository {

    private static final Logger log = LoggerFactory.getLogger(Player_Repository.class);
    private final Firestore db;

    // --- CONSTRUCTOR ---
    public Player_Repository() {
        this.db = FirestoreClient.getFirestore();
        log.info("Player_Repository initialised");
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


    // --- GET ---
    public Player getPlayer(String playerID) throws Exception {
        log.info("Fetching player with ID: {}", playerID);

        var document = db.collection("Player").document(playerID).get().get();
        if (!document.exists()) {
            throw new Exception("Player ID: " + playerID + " not found.");
        }

        // 1. Basic fields
        String first  = document.getString("firstName");
        String last   = document.getString("lastName");
        String nick   = document.getString("nickName");
        Boolean isCap = document.getBoolean("isCaptain");

        // 2. Reconstruct Player
        Player p = new Player(first, last);
        p.setName(first, last, nick);
        if (Boolean.TRUE.equals(isCap)) p.makeCaptain();

        // 3. Retrieve all stat entries for this player
        var statDocs = db.collection("stats")
                .whereEqualTo("holderID", playerID)
                .get()
                .get();

        for (var statDoc : statDocs.getDocuments()) {
            int eventID       = Integer.parseInt(Objects.requireNonNull(statDoc.getString("eventID")));
            String teamIDStr  = statDoc.getString("teamID");
            Integer teamID    = (teamIDStr == null || teamIDStr.equals("null"))
                    ? null : Integer.parseInt(teamIDStr);

            BaseStats_Key key = new BaseStats_Key(eventID, teamID);
            BaseStats stats   = p.getOrCreateStats(key);

            stats.set(StatField.FRAME_WIN,        toInt(statDoc.getLong("FRAME_WIN")));
            stats.set(StatField.FRAME_LOSS,       toInt(statDoc.getLong("FRAME_LOSS")));
            stats.set(StatField.FRAME_BREAK_DISH, toInt(statDoc.getLong("FRAME_BREAK_DISH")));
            stats.set(StatField.FRAME_TOTAL,      toInt(statDoc.getLong("FRAME_TOTAL")));
            stats.set(StatField.MATCH_WIN,        toInt(statDoc.getLong("MATCH_WIN")));
            stats.set(StatField.MATCH_LOSS,       toInt(statDoc.getLong("MATCH_LOSS")));
            stats.set(StatField.MATCH_DRAW,       toInt(statDoc.getLong("MATCH_DRAW")));
            stats.set(StatField.MATCH_TOTAL,      toInt(statDoc.getLong("MATCH_TOTAL")));
        }

        log.info("Player {} retrieved with {} stat entries.", p.getFullName(), statDocs.size());
        return p;
    }


    // --- VERIFY ---
    public void verifyPlayer(Player memoryPlayer, Player firebasePlayer) {
        log.info("=== PLAYER VERIFICATION: {} ===", memoryPlayer.getFullName());

        // Basic fields
        log.info("  firstName : memory={} | firebase={} | {}",
                memoryPlayer.getFirstName(), firebasePlayer.getFirstName(),
                Objects.equals(memoryPlayer.getFirstName(), firebasePlayer.getFirstName()) ? "OK" : "MISMATCH");
        log.info("  lastName  : memory={} | firebase={} | {}",
                memoryPlayer.getLastName(), firebasePlayer.getLastName(),
                Objects.equals(memoryPlayer.getLastName(), firebasePlayer.getLastName()) ? "OK" : "MISMATCH");
        log.info("  nickName  : memory={} | firebase={} | {}",
                memoryPlayer.getNickName(), firebasePlayer.getNickName(),
                Objects.equals(memoryPlayer.getNickName(), firebasePlayer.getNickName()) ? "OK" : "MISMATCH");
        log.info("  isCaptain : memory={} | firebase={} | {}",
                memoryPlayer.isCaptain(), firebasePlayer.isCaptain(),
                memoryPlayer.isCaptain() == firebasePlayer.isCaptain() ? "OK" : "MISMATCH");

        // Stats
        Map<BaseStats_Key, BaseStats> memoryStats   = memoryPlayer.getStatsMap();
        Map<BaseStats_Key, BaseStats> firebaseStats = firebasePlayer.getStatsMap();

        log.info("  stat entries : memory={} | firebase={} | {}",
                memoryStats.size(), firebaseStats.size(),
                memoryStats.size() == firebaseStats.size() ? "OK" : "MISMATCH");

        boolean allStatsMatch = true;

        for (Map.Entry<BaseStats_Key, BaseStats> entry : memoryStats.entrySet()) {
            BaseStats_Key key          = entry.getKey();
            BaseStats     memoryStat   = entry.getValue();
            BaseStats     firebaseStat = firebaseStats.get(key);

            if (firebaseStat == null) {
                log.warn("  MISSING key in firebase: eventID={}, teamID={}", key.eventID(), key.teamID());
                allStatsMatch = false;
                continue;
            }

            for (StatField field : StatField.values()) {
                int m = memoryStat.get(field);
                int f = firebaseStat.get(field);
                if (m != f) {
                    log.warn("  MISMATCH key=({},{}) field={} | memory={} firebase={}",
                            key.eventID(), key.teamID(), field, m, f);
                    allStatsMatch = false;
                }
            }
        }

        for (BaseStats_Key key : firebaseStats.keySet()) {
            if (!memoryStats.containsKey(key)) {
                log.warn("  EXTRA key in firebase (not in memory): eventID={}, teamID={}", key.eventID(), key.teamID());
                allStatsMatch = false;
            }
        }

        log.info("  STATS: {}", allStatsMatch ? "ALL MATCH" : "MISMATCHES FOUND");
        log.info("=== END VERIFICATION: {} ===", memoryPlayer.getFullName());
    }


    // --- HELPERS ---
    private int toInt(Long value) {
        return value != null ? value.intValue() : 0;
    }
}