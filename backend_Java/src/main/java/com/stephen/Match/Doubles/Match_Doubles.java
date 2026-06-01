package com.stephen.Match.Doubles;

import com.stephen.Doubles.Doubles;
import com.stephen.Match.Match;
import jakarta.persistence.*;

@Entity
@Table(name = "matches_doubles")
@DiscriminatorValue("DOUBLES")
public class Match_Doubles extends Match {

    @ManyToOne
    @JoinColumn(name = "doubles_a_id", nullable = false)
    private Doubles doublesA;

    @ManyToOne
    @JoinColumn(name = "doubles_b_id", nullable = false)
    private Doubles doublesB;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Doubles winner;

    @ManyToOne
    @JoinColumn(name = "loser_id")
    private Doubles loser;

    protected Match_Doubles() {}

    public Match_Doubles(Doubles doublesA, Doubles doublesB) {
        this.doublesA = doublesA;
        this.doublesB = doublesB;
    }

    /**
     * Bye constructor — used by Tournament_Service when one party slot is a bye.
     * The real doubles pair is stored as doublesA; doublesB is left null.
     * setBye(true) is called by the service after construction.
     */
    public Match_Doubles(Doubles realDoubles) {
        this.doublesA = realDoubles;
        this.doublesB = null;
    }

    public Doubles getDoublesA() { return doublesA; }
    public Doubles getDoublesB() { return doublesB; }

    public Doubles getWinner() { return winner; }
    public void setWinner(Doubles winner) { this.winner = winner; }

    public Doubles getLoser() { return loser; }
    public void setLoser(Doubles loser) { this.loser = loser; }
}