package com.stephen.Frame;

import com.stephen.Doubles.Doubles;
import com.stephen.Player.Player;
import com.stephen.BaseStats.BaseStats_Key;
import com.stephen.BaseStats.BaseStats_Service;
import com.stephen.BaseStats.StatField;
import com.stephen.BaseStats.StatHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;


public class Frame_Doubles <S extends StatHolder<S>> extends Frame<Doubles> {
    private final Player teamAPlayer1;
    private final Player teamAPlayer2;
    private final Player teamBPlayer1;
    private final Player teamBPlayer2;
    private final BaseStats_Key frameKeyA;
    private final BaseStats_Key frameKeyB;
    private static final Logger log = LoggerFactory.getLogger(Frame_Doubles.class);


    // --- CONSTRUCTOR ---
    public Frame_Doubles(Doubles doubles1, Doubles doubles2){
        super(doubles1, doubles2);
        this.teamAPlayer1 = doubles1.getPlayers().getFirst();
        this.teamAPlayer2 = doubles1.getPlayers().getLast();
        this.teamBPlayer1 = doubles2.getPlayers().getFirst();
        this.teamBPlayer2 = doubles2.getPlayers().getLast();        
        this.frameKeyA = new BaseStats_Key(super.getID(), doubles1.getID());
        this.frameKeyB = new BaseStats_Key(super.getID(), doubles2.getID());
    }

    public Frame_Doubles(Doubles doubles){
        super(doubles, new Doubles());
        this.teamAPlayer1 = doubles.getPlayers().getFirst();
        this.teamAPlayer2 = doubles.getPlayers().getLast();
        this.teamBPlayer1 = new Player();
        this.teamBPlayer2 = new Player();  
        this.frameKeyA = new BaseStats_Key(super.getID(), doubles.getID());
        this.frameKeyB = new BaseStats_Key(super.getID(), 0);
    }

    public Frame_Doubles(){
        super(new Doubles(), new Doubles());
        this.teamAPlayer1 = new Player();
        this.teamAPlayer2 = new Player();
        this.teamBPlayer1 = new Player();
        this.teamBPlayer2 = new Player();  
        this.frameKeyA = new BaseStats_Key(super.getID(), 0);
        this.frameKeyB = new BaseStats_Key(super.getID(), 0);
    }


    // --- INTERFACE ---
    @Override
    public void PlayOutFrame() {
        this.handleBye(super.getParty1(), super.getParty2());
        this.playFrame();
        log.info("Played out Frame_Doubles with ID: {}", super.getID());
    }

    @Override
    public void recordFrame() {
        recordDoublesTeam_Frame();
        recordDoublesPlayer_Frame();
        updateCloud_Frame();
        log.info("Recorded Frame_Doubles with ID: {}", super.getID());
    }


    // --- GETTERS ---
    public Player getTeamAPlayerA(){
        return this.teamAPlayer1;
    }
    
    public Player getTeamAPlayerB(){
        return this.teamAPlayer2;
    }

    public Player getTeamBPlayerA(){
        return this.teamBPlayer1;
    }

    public Player getTeamBPlayerB(){
        return this.teamBPlayer2;
    }

    @Override
    public ArrayList<Player> getPlayersA(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(getTeamAPlayerA());
        players.add(getTeamAPlayerB());
        return players;
    }

    @Override
    public ArrayList<Player> getPlayersB(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(getTeamBPlayerA());
        players.add(getTeamBPlayerB());
        return players;
    }

    public ArrayList<Player> getAllPlayers(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(getTeamAPlayerA());
        players.add(getTeamAPlayerB());
        players.add(getTeamBPlayerA());
        players.add(getTeamBPlayerB());
        return players;
    }


    // --- LOGIC ---
    public void recordDoublesPlayer_Frame(){
        BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_TOTAL, this.teamAPlayer1);
        BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_TOTAL, this.teamAPlayer2);
        BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_TOTAL, this.teamBPlayer1);
        BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_TOTAL, this.teamBPlayer2);
        if (this.getWinner().equals(this.getParty1())) {
            BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_WIN, this.teamAPlayer1);
            BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_WIN, this.teamAPlayer2);
            BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_LOSS, this.teamBPlayer1);
            BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_LOSS, this.teamBPlayer2);
            if (this.isBreakDish()) {
                BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_BREAK_DISH, this.teamAPlayer1);
                BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_BREAK_DISH, this.teamAPlayer2);
            }
        } else {
            BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_LOSS, this.teamAPlayer1);
            BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_LOSS, this.teamAPlayer2);
            BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_WIN, this.teamBPlayer1);
            BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_WIN, this.teamBPlayer2);
            if (this.isBreakDish()) {
                BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_BREAK_DISH, this.teamBPlayer1);
                BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_BREAK_DISH, this.teamBPlayer2);
            }
        }

        log.info("Recorded player stats for Frame_Doubles with ID: {}", super.getID());
    }

    public void recordDoublesTeam_Frame(){
        Doubles teamA = this.getParty1();
        Doubles teamB = this.getParty2();
        BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_TOTAL, teamA);
        BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_TOTAL, teamB);
        BaseStats_Service.applyFrame_WIN_LOSS(frameKeyA, frameKeyB, this);
        log.info("Recorded team stats for Frame_Doubles with ID: {}", super.getID());
    }

}
