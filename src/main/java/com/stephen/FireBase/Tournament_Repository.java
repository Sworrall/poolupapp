package com.stephen.FireBase;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stephen.Match.Match;
import com.stephen.BaseStats.StatHolder;
import com.stephen.Tournament.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Tournament_Repository<S extends StatHolder<S>> {

    private static final Logger log = LoggerFactory.getLogger(Tournament_Repository.class);
    private final Firestore db;

    // --- CONSTRUCTOR ---
    public Tournament_Repository(Tournament<S> tournament) {
        this.db = FirestoreClient.getFirestore();
        log.info("Tournament_Repository initialised");
    }

    public void saveTournament(Tournament<S> tournament) {
        try {
            log.info("Attempting to save Tournament ID: {}. Type:{}", tournament.getID(), tournament.getTournamentType());

            Map<String, Object> data = new HashMap<>();
            List<String> matchIDs = new ArrayList<>();
            ArrayList<ArrayList<Match<S>>> matches = tournament.getMatches();
            for(ArrayList<Match<S>> matchList : matches){
                for(Match<S> m : matchList){
                    matchIDs.add(String.valueOf(m.getID()));
                }
            }
            data.put("matchIDs", matchIDs);
            data.put("type", tournament.getTournamentType());
            data.put("isComplete", tournament.isComplete());
            data.put("place1", tournament.isComplete() ? String.valueOf(tournament.getPlace1().getID()) : null);
            data.put("place2", tournament.isComplete() ? String.valueOf(tournament.getPlace2().getID()) : null);
            data.put("place3", tournament.isComplete() ? String.valueOf(tournament.getPlace3().getID()) : null);
            data.put("place4", tournament.isComplete() ? String.valueOf(tournament.getPlace4().getID()) : null);
            log.info("Data map built: {}", data);

            db.collection("Tournament")
                    .document(String.valueOf(tournament.getID()))
                    .set(data)
                    .get();

            log.info("Firestore write confirmed for Tournament: {} type: {}", tournament.getID(), tournament.getTournamentType());

        } catch (Exception e) {
            log.error("Failed to save Tournament: {} type: {}", tournament.getID(), tournament.getTournamentType(), e);
        }
    }
}
