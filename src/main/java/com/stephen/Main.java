package com.stephen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("PoolManager application starting...");
        FirebaseConfig.initialise();

        // Create and save a player
        Player player1 = new Player("Dan", "Worrall");
        Player player2 = new Player("John", "Smith");
        Player_Repository playerRepo = new Player_Repository();
        playerRepo.savePlayer(player1);
        playerRepo.savePlayer(player2);

        // Create and save a team
        Team team = new Team("The A Team");
        team.addPlayer(player1);
        team.addPlayer(player2);
        Team_Repository teamRepo = new Team_Repository();
        teamRepo.saveTeam(team);

        log.info("Done!");
    }
}