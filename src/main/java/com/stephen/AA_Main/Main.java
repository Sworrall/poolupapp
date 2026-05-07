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


        // Create players
        Player player1 = new Player("Joe", "Bloggs");
        Player player2 = new Player("John", "Smith");
        Player player3 = new Player("Steve", "Jones");
        Player player4 = new Player("Mike", "Brown");


        // Create Teams
        Team team1 = new Team("The A Team");
        team1.addPlayer(player1);
        team1.addPlayer(player2);
        Team team2 = new Team("The B Team");
        team2.addPlayer(player3);
        team2.addPlayer(player4);


        // create and save Doubles Team
        Doubles doublesTeam1 = new Doubles("Double the Trouble");
        doublesTeam1.addPlayer(player1);
        doublesTeam1.addPlayer(player2);

        Doubles doublesTeam2 = new Doubles("Double Bubble");
        doublesTeam2.addPlayer(player3);
        doublesTeam2.addPlayer(player4);


        // create Frame<Player>. save, then play and overwrite
        Frame<Player> frame = new Frame_Singles(player1, player2);
        log.info("FRAME ADDED");
        frame.playOutFrame();
        log.info("FRAME PLAYED AND UPDATED");


        // create Match<Doubles> save, play and save again
        FrameFactory<Doubles> doublesFactory = new FrameFactory_Doubles();
        Match<Doubles> doublesMatch = new Match_Doubles(doublesTeam1, doublesTeam2, 11, doublesFactory);
        log.info("DOUBLES MATCH CREATED");
        doublesMatch.playOutMatch();
        log.info("DOUBLES MATCH PLAYED AND UPDATED");


        // upload a tournament
        ArrayList<Team> tournamentTeams = new ArrayList<>();
        tournamentTeams.add(team1);
        tournamentTeams.add(team2);
        Match_Factory<Team> mf = new MatchFactory_Team();
        Tournament_RoundRobin<Team> RR = new Tournament_RoundRobin<>(tournamentTeams, 11, mf);


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