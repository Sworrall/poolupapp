package com.stephen;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;

public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    public static void initialise() {
        try {
            FileInputStream serviceAccount = new FileInputStream(
                    "poolapp-ca624-firebase-adminsdk-fbsvc-2b87049878.json"
            );

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);

            log.info("Firebase connected successfully!");

        } catch (IOException e) {
            log.error("Failed to connect to Firebase", e);
        }
    }
}