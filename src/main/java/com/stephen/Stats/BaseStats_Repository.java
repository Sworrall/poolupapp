package com.stephen.Stats;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stephen.Doubles.Doubles;
import com.stephen.Player.Player;
import com.stephen.Team.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BaseStats_Repository<S extends StatHolder<S>> {

    private static final Logger log = LoggerFactory.getLogger(BaseStats_Repository.class);
    private final Firestore db;

    public BaseStats_Repository() {
        this.db = FirestoreClient.getFirestore();
        log.info("BaseStats_Repository initialised");
    }

    public void saveStats(Player player) {
        player.getStatsMap().forEach((key, stats) -> {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("holderID", String.valueOf(player.getID()));
                data.put("eventID", String.valueOf(key.eventID()));
                data.put("teamID", key.teamID() != null ? String.valueOf(key.teamID()) : "null");
                data.put("FRAME_WIN", stats.get(StatField.FRAME_WIN));
                data.put("FRAME_LOSS", stats.get(StatField.FRAME_LOSS));
                data.put("FRAME_BREAK_DISH", stats.get(StatField.FRAME_BREAK_DISH));
                data.put("FRAME_TOTAL", stats.get(StatField.FRAME_TOTAL));
                data.put("MATCH_WIN", stats.get(StatField.MATCH_WIN));
                data.put("MATCH_LOSS", stats.get(StatField.MATCH_LOSS));
                data.put("MATCH_DRAW", stats.get(StatField.MATCH_DRAW));
                data.put("MATCH_TOTAL", stats.get(StatField.MATCH_TOTAL));
                String docID = player.getID() + "_" + key.eventID() + "_" +
                        (key.teamID() != null ? key.teamID() : "null");

                db.collection("stats")
                        .document(docID)
                        .set(data)
                        .get();

                log.info("Stats saved: {}", docID);

            } catch (Exception e) {
                log.error("Failed to save stats: {}", e.getMessage());
            }
        });
    }

    public void saveStats(Doubles doubles) {
        doubles.getStatsMap().forEach((key, stats) -> {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("holderID", String.valueOf(doubles.getID()));
                data.put("eventID", String.valueOf(key.eventID()));
                data.put("teamID", String.valueOf(key.teamID()));
                data.put("FRAME_WIN", stats.get(StatField.FRAME_WIN));
                data.put("FRAME_LOSS", stats.get(StatField.FRAME_LOSS));
                data.put("FRAME_BREAK_DISH", stats.get(StatField.FRAME_BREAK_DISH));
                data.put("FRAME_TOTAL", stats.get(StatField.FRAME_TOTAL));
                data.put("MATCH_WIN", stats.get(StatField.MATCH_WIN));
                data.put("MATCH_LOSS", stats.get(StatField.MATCH_LOSS));
                data.put("MATCH_DRAW", stats.get(StatField.MATCH_DRAW));
                data.put("MATCH_TOTAL", stats.get(StatField.MATCH_TOTAL));
                String docID = doubles.getID() + "_" + key.eventID() + "_" +
                        (key.teamID() != null ? key.teamID() : "null");

                db.collection("stats")
                        .document(docID)
                        .set(data)
                        .get();

                log.info("Stats saved: {}", docID);

            } catch (Exception e) {
                log.error("Failed to save stats: {}", e.getMessage());
            }
        });
    }

    public void saveStats(Team team) {
        team.getStatsMap().forEach((key, stats) -> {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("holderID", String.valueOf(team.getID()));
                data.put("eventID", String.valueOf(key.eventID()));
                data.put("teamID", String.valueOf(key.teamID()));
                data.put("FRAME_WIN", stats.get(StatField.FRAME_WIN));
                data.put("FRAME_LOSS", stats.get(StatField.FRAME_LOSS));
                data.put("FRAME_BREAK_DISH", stats.get(StatField.FRAME_BREAK_DISH));
                data.put("FRAME_TOTAL", stats.get(StatField.FRAME_TOTAL));
                data.put("MATCH_WIN", stats.get(StatField.MATCH_WIN));
                data.put("MATCH_LOSS", stats.get(StatField.MATCH_LOSS));
                data.put("MATCH_DRAW", stats.get(StatField.MATCH_DRAW));
                data.put("MATCH_TOTAL", stats.get(StatField.MATCH_TOTAL));
                String docID = team.getID() + "_" + key.eventID() + "_" +
                        (key.teamID() != null ? key.teamID() : "null");

                db.collection("stats")
                        .document(docID)
                        .set(data)
                        .get();

                log.info("Stats saved: {}", docID);

            } catch (Exception e) {
                log.error("Failed to save stats: {}", e.getMessage());
            }
        });
    }
}
