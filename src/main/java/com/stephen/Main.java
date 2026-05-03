package com.stephen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main{

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("PoolManager application starting...");
        FirebaseConfig.initialise();

        // Create and save Players
        Player_Repository playerRepo = new Player_Repository();
        Player player1 = new Player("Joe", "Bloggs");
        Player player2 = new Player("John", "Smith");
        Player player3 = new Player("Steve", "Jones");
        Player player4 = new Player("Mike", "Brown");
        playerRepo.savePlayer(player1);
        playerRepo.savePlayer(player2);
        playerRepo.savePlayer(player3);
        playerRepo.savePlayer(player4);

        // Create and save a Team
        Team_Repository teamRepo = new Team_Repository();
        Team team1 = new Team("The A Team");
        team1.addPlayer(player1);
        team1.addPlayer(player2);
        teamRepo.saveTeam(team1);

        Team team2 = new Team("The B Team");
        team2.addPlayer(player3);
        team2.addPlayer(player4);
        teamRepo.saveTeam(team2);

        // create and save Doubles Team
        Doubles_Repository doublesRepo = new Doubles_Repository();
        Doubles doublesTeam1 = new Doubles("Double the Trouble");
        doublesTeam1.addPlayer(player1);
        doublesTeam1.addPlayer(player2);
        doublesRepo.saveDoublesTeam(doublesTeam1);
        Doubles doublesTeam2 = new Doubles("Double Bubble");
        doublesTeam2.addPlayer(player3);
        doublesTeam2.addPlayer(player4);
        doublesRepo.saveDoublesTeam(doublesTeam2);


        // create Frame<Player>. save, then play and overwrite
        Frame_Repository<Player> frameRepo = new Frame_Repository<>();
        Frame<Player> frame = new Frame_Singles<>(player1, player2);
        frameRepo.saveFrame(frame);
        log.info("FRAME ADDED");
        frame.playFrame();
        frameRepo.saveFrame(frame);
        log.info("FRAME PLAYED");

        // create Match<Doubles> save, play and save again
        Match_Repository<Doubles> matchRepo = new Match_Repository<>();
        FrameFactory<Doubles> doublesFactory = new FrameFactory_Doubles();
        Match<Doubles> doublesMatch = new Match_Doubles(doublesTeam1, doublesTeam2, 11, doublesFactory);
        matchRepo.saveMatch(doublesMatch);
        log.info("DOUBLES MATCH CREATED");
        doublesMatch.playMatch();
        matchRepo.saveMatch(doublesMatch);
        log.info("DOUBLES MATCH PLAYED");

        // stats have been created for player 1. upload them all to Firebase
        var baseStatRepo = new BaseStats_Repository<Player>();
        baseStatRepo.saveStats(player1);
        baseStatRepo.saveStats(player2);
        baseStatRepo.saveStats(player3);
        baseStatRepo.saveStats(player4);
        baseStatRepo.saveStats(team1);
        baseStatRepo.saveStats(team2);
        baseStatRepo.saveStats(doublesTeam1);
        baseStatRepo.saveStats(doublesTeam2);
        log.info("BASE STATS uploaded");


        log.info("Done!");
    }
}