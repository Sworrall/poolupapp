package com.stephen.FireBase;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stephen.Frame.Frame;
import com.stephen.Match.Match;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match_Repository<S extends StatHolder<S>> {

    private static final Logger log = LoggerFactory.getLogger(Match_Repository.class);
    private final Firestore db;


    //--- CONSTRUCTOR ---
    public Match_Repository() {
        this.db = FirestoreClient.getFirestore();
        log.info("Match_Repository initialised");
    }


    // --- SAVE ---
    public void saveMatch(Match<S> match) {
        try {
            log.info("Attempting to save Match<{}>: {}", match.getMatchType(), match.getID());

            Map<String, Object> data = new HashMap<>();
            data.put("frameCount", match.getFrameCount());

            List<String> frameIDList = new ArrayList<>();
            for(Frame<S> frame : match.getFrames()){
                frameIDList.add(String.valueOf(frame.getID()));
            }
            data.put("frameIDs", frameIDList);
            data.put("type", match.getMatchType());
            data.put("party1ID", String.valueOf(match.getParty1().getID()));
            data.put("party2ID", String.valueOf(match.getParty2().getID()));
            data.put("winnerID", match.isPlayed() ? String.valueOf(match.getWinner().getID()) : null);
            data.put("loserID", match.isPlayed() ? String.valueOf(match.getLoser().getID()) : null);
            data.put("isBye", match.isBye());
            data.put("isPlayed", match.isPlayed());
            data.put("isDraw", match.isDraw());
            log.info("Data map built: {}", data);

            db.collection("Match")
                    .document(String.valueOf(match.getID()))
                    .set(data)
                    .get();

            log.info("Firestore write confirmed for Match<{}>: {}", match.getMatchType(), match.getID());

        } catch (Exception e) {
            log.error("Failed to save Match<{}>: {}", match.getMatchType(), match.getID(), e);
        }
    }
}


