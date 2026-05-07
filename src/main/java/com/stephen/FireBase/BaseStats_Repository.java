package com.stephen.FireBase;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stephen.BaseStats.StatField;
import com.stephen.BaseStats.StatHolder;
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

    public void saveStats(S statHolder) {
        statHolder.getStatsMap().forEach((key, stats) -> {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("holderID", String.valueOf(statHolder.getID()));
                data.put("eventID", String.valueOf(key.eventID()));
                data.put("teamID", key.teamID() != null ? String.valueOf(key.teamID()) : "null");

                for (StatField field : StatField.values()) {
                    data.put(field.name(), stats.get(field));
                }

                String docID = statHolder.getID() + "_" + key.eventID() + "_" +
                        (key.teamID() != null ? key.teamID() : "null");

                db.collection("stats")
                        .document(docID)
                        .set(data)
                        .get();

                log.info("Stats saved: {}", docID);

            } catch (Exception e) {
                log.error("Failed to save stats for {}: {}", statHolder.getName(), e.getMessage());
            }
        });
    }
}
