package com.stephen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("PoolManager application starting...");
        FirebaseConfig.initialise();

        // Test saving a player
        Player player = new Player("Gerald", "Clarkson");
        PlayerRepository playerRepo = new PlayerRepository();
        playerRepo.savePlayer(player);

        log.info("Done!");
    }
}