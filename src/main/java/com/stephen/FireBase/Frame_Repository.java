package com.stephen.FireBase;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.stephen.Frame.Frame;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Frame_Repository <S extends StatHolder<S>>{

    private static final Logger log = LoggerFactory.getLogger(Frame_Repository.class);
    private final Firestore db;


    //--- CONSTRUCTOR ---
    public Frame_Repository() {
        this.db = FirestoreClient.getFirestore();
        log.info("Frame_Repository initialised");
    }


    // --- SAVE ---
    public void saveFrame(Frame<S> frame) {
        try {
            log.info("Attempting to save Frame<{}>: {}", frame.getFrameType(), frame.getID());

            Map<String, Object> data = new HashMap<>();
            data.put("type", frame.getFrameType());
            data.put("party1Id", String.valueOf(frame.getParty1().getID()));
            data.put("party2Id", String.valueOf(frame.getParty2().getID()));
            data.put("winnerId", frame.isPlayed() ? String.valueOf(frame.getWinner().getID()) : null);
            data.put("loserId", frame.isPlayed() ? String.valueOf(frame.getLoser().getID()) : null);
            data.put("isBye", frame.isBye());
            data.put("isPlayed", frame.isPlayed());
            if(!frame.isPlayed()){
                data.put("isBreakDish", false);
            }else if(frame.isPlayed()){
                data.put("isBreakDish", frame.isBreakDish());
            }

            log.info("Data map built: {}", data);

            db.collection("Frame")
                    .document(String.valueOf(frame.getID()))
                    .set(data)
                    .get();

            log.info("Firestore write confirmed for Frame<{}>: {}", frame.getFrameType(), frame.getID());

        } catch (Exception e) {
            log.error("Failed to save Frame<{}>: {}", frame.getFrameType(), frame.getID(), e);
        }
    }
}


