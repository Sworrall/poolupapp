package com.stephen.AA_Main;

import com.stephen.Doubles.Doubles;
import com.stephen.FireBase.*;
import com.stephen.Frame.Frame;
import com.stephen.Frame.FrameFactory.FrameFactory;
import com.stephen.Frame.FrameFactory.FrameFactory_Doubles;
import com.stephen.Frame.Frame_Singles;
import com.stephen.Match.Match;
import com.stephen.Match.MatchFactory.MatchFactory_Team;
import com.stephen.Match.MatchFactory.Match_Factory;
import com.stephen.Match.Match_Doubles;
import com.stephen.Player.*;
import com.stephen.Team.Team;
import com.stephen.Tournament.Tournament_RoundRobin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;


public class Main{

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        log.info("PoolManager application starting...");
        FirebaseConfig.initialise();


        // Create and save Players
        Player player1 = new Player("Joe", "Bloggs");
        player1.updateCloud_All();
        Player player2 = new Player("John", "Smith");
        player2.updateCloud_All();
        Player player3 = new Player("Steve", "Jones");
        player3.updateCloud_All();
        Player player4 = new Player("Mike", "Brown");
        player4.updateCloud_All();


        // Create and save a Team
        Team team1 = new Team("The A Team");
        team1.addPlayer(player1);
        team1.addPlayer(player2);
        team1.updateCloud_All();

        Team team2 = new Team("The B Team");
        team2.addPlayer(player3);
        team2.addPlayer(player4);
        team2.updateCloud_All();


        // create and save Doubles Team
        Doubles doublesTeam1 = new Doubles("Double the Trouble");
        doublesTeam1.addPlayer(player1);
        doublesTeam1.addPlayer(player2);
        doublesTeam1.updateCloud_All();

        Doubles doublesTeam2 = new Doubles("Double Bubble");
        doublesTeam2.addPlayer(player3);
        doublesTeam2.addPlayer(player4);
        doublesTeam2.updateCloud_All();


        // create Frame<Player>. save, then play and overwrite
        Frame<Player> frame = new Frame_Singles(player1, player2);
        frame.updateCloud_Frame();
        log.info("FRAME ADDED");

        frame.playFrame();
        frame.recordFrame();
        log.info("FRAME PLAYED AND UPDATED");


        // create Match<Doubles> save, play and save again
        FrameFactory<Doubles> doublesFactory = new FrameFactory_Doubles();
        Match<Doubles> doublesMatch = new Match_Doubles(doublesTeam1, doublesTeam2, 11, doublesFactory);
        doublesMatch.recordMatch();
        log.info("DOUBLES MATCH CREATED");

        doublesMatch.playMatch();
        for(Frame<Doubles> f : doublesMatch.getFrames()) {
            f.recordFrame();
            f.updateCloud_Frame();
        }
        doublesMatch.recordMatch();
        log.info("DOUBLES MATCH PLAYED AND UPDATED");


        // stats have been created for player 1. upload them all to Firebase
        BaseStats_Repository<Player> baseStatRepo = new BaseStats_Repository<>();
        player1.updateCloud_Stats();
        player2.updateCloud_Stats();
        player3.updateCloud_Stats();
        player4.updateCloud_Stats();
        team1.updateCloud_Stats();
        team2.updateCloud_Stats();
        doublesTeam1.updateCloud_Stats();
        doublesTeam2.updateCloud_Stats();
        log.info("BASE STATS uploaded");

        // upload a tournament
        ArrayList<Team> tournamentTeams = new ArrayList<>();
        tournamentTeams.add(team1);
        tournamentTeams.add(team2);
        Match_Factory<Team> mf = new MatchFactory_Team();
        Tournament_RoundRobin<Team> RR = new Tournament_RoundRobin<>(tournamentTeams, 11, mf);
        RR.updateCloud_Tournament();


        // grab a player from the cloud and store. compare values to existing player
        Player_Repository playerRepo = new Player_Repository();
        Player firebasePlayer1 = playerRepo.getPlayer(String.valueOf(player1.getID()));
        playerRepo.verifyPlayer(player1, firebasePlayer1);

        // grab a player from the cloud and store. compare stats to existing player


        // do this for doubles and team - and stats


        // do this for frame - singles / doubles / team / killer - and stats


        // again for match - singles / doubles / team - and stats


        // then finally tournament - and stats


        log.info("Done!");
    }
}