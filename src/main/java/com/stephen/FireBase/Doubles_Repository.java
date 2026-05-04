package com.stephen.FireBase;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stephen.Functions.DataCallback;
import com.stephen.Doubles.Doubles;
import com.stephen.Player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Doubles_Repository {
    private static final Logger log = LoggerFactory.getLogger(Doubles_Repository.class);
    private final Firestore db;

    //--- CONSTRUCTOR ---
    public Doubles_Repository() {
        this.db = FirestoreClient.getFirestore();
        log.info("Doubles_Repository initialised");
    }

    // --- SAVE ---
    public void saveDoublesTeam(Doubles doubles) {
        try {
            log.info("Attempting to save Doubles Team: {}", doubles.getName());

            Map<String, Object> data = new HashMap<>();
            data.put("teamName", doubles.getName());
            data.put("isBye", doubles.isBye());
            data.put("captainID", doubles.getCaptain() != null
                    ? String.valueOf(doubles.getCaptain().getID())
                    : null);
            data.put("playerIDs", doubles.getPlayers().stream()
                    .map(p -> String.valueOf(p.getID()))
                    .collect(Collectors.toList()));

            log.info("Data map built: {}", data);

            db.collection("Doubles")
                    .document(String.valueOf(doubles.getID()))
                    .set(data)
                    .get();

            log.info("Firestore write confirmed for Doubles Team: {}", doubles.getName());

        } catch (Exception e) {
            log.error("Failed to save Doubles Team: {}", doubles.getName(), e);
        }
    }

    // Add this to your Doubles_Repository class
    public void getDoublesTeam(String id, DataCallback<Doubles> callback) {
        db.collection("Doubles").document(id).get().addListener(() -> {
            try {
                // Note the parentheses here!
                DocumentSnapshot doc = db.collection("Doubles").document(id).get().get();

                if (doc.exists()) {
                    Doubles team = new Doubles(doc.getString("teamName"));
                } else {
                    callback.onError(new Exception("Team not found"));
                }
            } catch (Exception e) {
                callback.onError(e); // Sends the error back to your UI logic
            }
        }, Runnable::run);
    }}


