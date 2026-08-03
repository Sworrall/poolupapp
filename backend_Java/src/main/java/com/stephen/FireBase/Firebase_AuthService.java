package com.stephen.FireBase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class Firebase_AuthService {

    private static final Logger log = LoggerFactory.getLogger(Firebase_AuthService.class);

    public FirebaseToken verifyToken(String idToken) throws Exception {
        FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        log.info("Token verified for user: {}", decoded.getUid());
        return decoded;
    }
}