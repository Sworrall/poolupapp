package com.stephen;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class PlayerRepository {

    private static final Logger log = LoggerFactory.getLogger(PlayerRepository.class);
    private final Firestore db;

    // --- CONSTRUCTOR ---
    public PlayerRepository() {
        this.db = FirestoreClient.getFirestore();
        log.info("PlayerRepository initialised");
    }

    // --- SAVE ---
    public void savePlayer(Player player) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("firstName", player.getFirstName());
            data.put("lastName", player.getLastName());
            data.put("nickName", player.getNickName());
            data.put("isBye", player.isBye());
            data.put("isCaptain", player.isCaptain());

            db.collection("Player")
                    .document(String.valueOf(player.getID()))
                    .set(data);

            log.info("Saved player: {}", player.getFullName());

        } catch (Exception e) {
            log.error("Failed to save player: {}", player.getFullName(), e);
        }
    }
}