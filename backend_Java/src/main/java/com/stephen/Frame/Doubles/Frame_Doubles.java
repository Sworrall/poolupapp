package com.stephen.Frame.Doubles;

import com.stephen.Doubles.Doubles;
import com.stephen.Frame.Frame;
import com.stephen.Player.Player;
import jakarta.persistence.*;

@Entity
@Table(name = "frames_doubles")
@DiscriminatorValue("DOUBLES")
public class Frame_Doubles extends Frame {

    // --- TEAMS ---
    @ManyToOne
    @JoinColumn(name = "doubles_a_id", nullable = false)
    private Doubles doublesA;

    @ManyToOne
    @JoinColumn(name = "doubles_b_id", nullable = false)
    private Doubles doublesB;

    // --- PLAYER SNAPSHOT ---
    @ManyToOne
    @JoinColumn(name = "doubles_a_player_a_id")
    private Player doublesAPlayerA;

    @ManyToOne
    @JoinColumn(name = "doubles_a_player_b_id")
    private Player doublesAPlayerB;

    @ManyToOne
    @JoinColumn(name = "doubles_b_player_a_id")
    private Player doublesBPlayerA;

    @ManyToOne
    @JoinColumn(name = "doubles_b_player_b_id")
    private Player doublesBPlayerB;

    // --- RESULT ---
    @ManyToOne
    @JoinColumn(name = "winner_doubles_id")
    private Doubles winnerDoubles;

    @ManyToOne
    @JoinColumn(name = "winner_player_a_id")
    private Player winnerPlayerA;

    @ManyToOne
    @JoinColumn(name = "winner_player_b_id")
    private Player winnerPlayerB;

    @ManyToOne
    @JoinColumn(name = "loser_doubles_id")
    private Doubles loserDoubles;

    @ManyToOne
    @JoinColumn(name = "loser_player_a_id")
    private Player loserPlayerA;

    @ManyToOne
    @JoinColumn(name = "loser_player_b_id")
    private Player loserPlayerB;

    protected Frame_Doubles() {}

    public Frame_Doubles(Doubles doublesA, Doubles doublesB) {
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
    public void setWinner(Doubles winner) {
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
    public Doubles getDoublesA() { return doublesA; }
    public Doubles getDoublesB() { return doublesB; }

    public Player getDoublesAPlayerA() { return doublesAPlayerA; }
    public Player getDoublesAPlayerB() { return doublesAPlayerB; }
    public Player getDoublesBPlayerA() { return doublesBPlayerA; }
    public Player getDoublesBPlayerB() { return doublesBPlayerB; }

    public Doubles getWinner() { return winnerDoubles; }
    public Player getWinnerPlayerA() { return winnerPlayerA; }
    public Player getWinnerPlayerB() { return winnerPlayerB; }

    public Doubles getLoserDoubles() { return loserDoubles; }
    public Player getLoserPlayerA() { return loserPlayerA; }
    public Player getLoserPlayerB() { return loserPlayerB; }
}