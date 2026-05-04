package com.stephen.FireBase;

import java.util.HashMap;
import java.util.Map;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
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
}