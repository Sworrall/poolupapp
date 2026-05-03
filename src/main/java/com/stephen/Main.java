package com.stephen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main{

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("PoolManager application starting...");
        FirebaseConfig.initialise();

        // Create and save Players
        Player player1 = new Player("Dan", "Worrall");
        Player player2 = new Player("John", "Smith");
        Player_Repository playerRepo = new Player_Repository();
        playerRepo.savePlayer(player1);
        playerRepo.savePlayer(player2);

        // Create and save a Team
        Team team = new Team("The A Team");
        team.addPlayer(player1);
        team.addPlayer(player2);
        Team_Repository teamRepo = new Team_Repository();
        teamRepo.saveTeam(team);

        // create and save Doubles Team
        Doubles doublesTeam = new Doubles("Double the Trouble");
        doublesTeam.addPlayer(player1);
        doublesTeam.addPlayer(player2);
        Doubles_Repository doublesRepo = new Doubles_Repository();
        doublesRepo.saveDoublesTeam(doublesTeam);

        // create Frame<Player>. save, then play and overwrite
        Frame<Player> frame = new Frame_Singles<>(player1, player2);
        Frame_Repository<Player> frameRepo = new Frame_Repository<>();
        frameRepo.saveFrame(frame);
        log.info("FRAME ADDED");
        frame.playFrame();
        frameRepo.saveFrame(frame);
        log.info("FRAME PLAYED");

        log.info("Done!");
    }
}