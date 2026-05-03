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
        Team team = new Team("The A Team");
        team.addPlayer(player1);
        team.addPlayer(player2);
        teamRepo.saveTeam(team);

        Team team2 = new Team("The B Team");
        team2.addPlayer(player3);
        team2.addPlayer(player4);
        teamRepo.saveTeam(team2);

        // create and save Doubles Team
        Doubles_Repository doublesRepo = new Doubles_Repository();
        Doubles doublesTeamA = new Doubles("Double the Trouble");
        doublesTeamA.addPlayer(player1);
        doublesTeamA.addPlayer(player2);
        doublesRepo.saveDoublesTeam(doublesTeamA);
        Doubles doublesTeamB = new Doubles("Double Bubble");
        doublesTeamB.addPlayer(player3);
        doublesTeamB.addPlayer(player4);
        doublesRepo.saveDoublesTeam(doublesTeamB);


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
        Match<Doubles> doublesMatch = new Match_Doubles(doublesTeamA, doublesTeamB, 11, doublesFactory);
        matchRepo.saveMatch(doublesMatch);
        log.info("DOUBLES MATCH CREATED");
        doublesMatch.playMatch();
        matchRepo.saveMatch(doublesMatch);
        log.info("DOUBLES MATCH PLAYED");



        log.info("Done!");
    }
}