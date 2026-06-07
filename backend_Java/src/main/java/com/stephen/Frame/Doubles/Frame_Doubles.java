package com.stephen.Frame.Doubles;

import com.stephen.Doubles.Doubles_Entity;
import com.stephen.Frame.Frame_Entity;
import com.stephen.Player.Player_Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "frames_doubles")
@DiscriminatorValue("DOUBLES")
public class Frame_Doubles extends Frame_Entity {

    // --- TEAMS ---
    @ManyToOne
    @JoinColumn(name = "doubles_a_id", nullable = false)
    private Doubles_Entity doublesA;

    @ManyToOne
    @JoinColumn(name = "doubles_b_id", nullable = false)
    private Doubles_Entity doublesB;

    // --- PLAYER SNAPSHOT ---
    @ManyToOne
    @JoinColumn(name = "doubles_a_player_a_id")
    private Player_Entity doublesAPlayerA;

    @ManyToOne
    @JoinColumn(name = "doubles_a_player_b_id")
    private Player_Entity doublesAPlayerB;

    @ManyToOne
    @JoinColumn(name = "doubles_b_player_a_id")
    private Player_Entity doublesBPlayerA;

    @ManyToOne
    @JoinColumn(name = "doubles_b_player_b_id")
    private Player_Entity doublesBPlayerB;

    // --- RESULT ---
    @ManyToOne
    @JoinColumn(name = "winner_doubles_id")
    private Doubles_Entity winnerDoubles;

    @ManyToOne
    @JoinColumn(name = "winner_player_a_id")
    private Player_Entity winnerPlayerA;

    @ManyToOne
    @JoinColumn(name = "winner_player_b_id")
    private Player_Entity winnerPlayerB;

    @ManyToOne
    @JoinColumn(name = "loser_doubles_id")
    private Doubles_Entity loserDoubles;

    @ManyToOne
    @JoinColumn(name = "loser_player_a_id")
    private Player_Entity loserPlayerA;

    @ManyToOne
    @JoinColumn(name = "loser_player_b_id")
    private Player_Entity loserPlayerB;

    protected Frame_Doubles() {}

    public Frame_Doubles(Doubles_Entity doublesA, Doubles_Entity doublesB) {
        this.doublesA = doublesA;
        this.doublesB = doublesB;
        // snapshot players at frame creation time
        if (!doublesA.isBye()) {
            this.doublesAPlayerA = doublesA.getPlayer1();
            this.doublesAPlayerB = doublesA.getPlayer2();
        }
        if (!doublesB.isBye()) {
            this.doublesBPlayerA = doublesB.getPlayer1();
            this.doublesBPlayerB = doublesB.getPlayer2();
        }
        if (doublesA.isBye() || doublesB.isBye()) {
            this.setBye(true);
        }
    }

    // --- RESULT HELPERS ---
    public void setWinner(Doubles_Entity winner) {
        this.winnerDoubles = winner;
        if (winner.equals(doublesA)) {
            this.winnerPlayerA = doublesAPlayerA;
            this.winnerPlayerB = doublesAPlayerB;
            this.loserDoubles = doublesB;
            this.loserPlayerA = doublesBPlayerA;
            this.loserPlayerB = doublesBPlayerB;
        } else {
            this.winnerPlayerA = doublesBPlayerA;
            this.winnerPlayerB = doublesBPlayerB;
            this.loserDoubles = doublesA;
            this.loserPlayerA = doublesAPlayerA;
            this.loserPlayerB = doublesAPlayerB;
        }
    }

    // --- GETTERS ---
    public Doubles_Entity getDoublesA() { return doublesA; }
    public Doubles_Entity getDoublesB() { return doublesB; }

    public Player_Entity getDoublesAPlayerA() { return doublesAPlayerA; }
    public Player_Entity getDoublesAPlayerB() { return doublesAPlayerB; }
    public Player_Entity getDoublesBPlayerA() { return doublesBPlayerA; }
    public Player_Entity getDoublesBPlayerB() { return doublesBPlayerB; }

    public Doubles_Entity getWinner() { return winnerDoubles; }
    public Player_Entity getWinnerPlayerA() { return winnerPlayerA; }
    public Player_Entity getWinnerPlayerB() { return winnerPlayerB; }

    public Doubles_Entity getLoserDoubles() { return loserDoubles; }
    public Player_Entity getLoserPlayerA() { return loserPlayerA; }
    public Player_Entity getLoserPlayerB() { return loserPlayerB; }
}