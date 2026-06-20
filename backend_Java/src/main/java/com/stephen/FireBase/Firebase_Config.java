package com.stephen.FireBase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.IOException;

@Component
public class Config {

    private static final Logger log = LoggerFactory.getLogger(Config.class);

    @PostConstruct
    public void initialise() {
        log.info("Firebase config initialised");
        try {
            InputStream serviceAccount = Config.class
                    .getClassLoader()
                    .getResourceAsStream("poolapp-ca624-firebase-adminsdk-fbsvc-2b87049878.json");

            if (serviceAccount == null) {
                log.error("Firebase credentials file not found!");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            log.info("Firebase connected successfully!");

        } catch (IOException e) {
            log.error("Failed to connect to Firebase", e);
        }
    }
}