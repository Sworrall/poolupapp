 package com.stephen.FireBase;

 import java.util.HashMap;
 import java.util.Map;
 import java.util.stream.Collectors;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stephen.Team.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


 public class Team_Repository {
    private static final Logger log = LoggerFactory.getLogger(Team_Repository.class);
    private final Firestore db;

    //--- CONSTRUCTOR ---
    public Team_Repository() {
        this.db = FirestoreClient.getFirestore();
        log.info("Team_Repository initialized");
    }

    // --- SAVE ---
    public void saveTeam(Team team) {
        try {
            log.info("Attempting to save Team: {}", team.getName());

            Map<String, Object> data = new HashMap<>();
            data.put("teamName", team.getTeamName());
            data.put("isBye", team.isBye());
            data.put("captainId", team.getCaptain() != null
                    ? String.valueOf(team.getCaptain().getID())
                    : null);
            data.put("playerID", team.getPlayers().stream()
                    .map(p -> String.valueOf(p.getID()))
                    .collect(Collectors.toList()));
            log.info("Data map built: {}", data);

            db.collection("Team")
                    .document(String.valueOf(team.getID()))
                    .set(data)
                    .get();
            log.info("Firestore write confirmed for team: {}", team.getTeamName());

        } catch (Exception e) {
            log.error("Failed to save team: {}", team.getTeamName(), e);
        }
    }
}


