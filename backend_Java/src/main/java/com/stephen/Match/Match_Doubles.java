package com.stephen.Match;

import com.stephen.Doubles.Doubles_Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "matches_doubles")
@DiscriminatorValue("DOUBLES")
public class Match_Doubles extends Match_Entity {

    @ManyToOne
    @JoinColumn(name = "doubles_a_id", nullable = false)
    private Doubles_Entity doublesA;

    @ManyToOne
    @JoinColumn(name = "doubles_b_id", nullable = false)
    private Doubles_Entity doublesB;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Doubles_Entity winner;

    @ManyToOne
    @JoinColumn(name = "loser_id")
    private Doubles_Entity loser;

    protected Match_Doubles() {}

    public Match_Doubles(Doubles_Entity doublesA, Doubles_Entity doublesB) {
        this.doublesA = doublesA;
        this.doublesB = doublesB;
    }

    /**
     * Bye constructor — used by Tournament_Service when one party slot is a bye.
     * The real doubles pair is stored as doublesA; doublesB is left null.
     * setBye(true) is called by the service after construction.
     */
    public Match_Doubles(Doubles_Entity realDoubles) {
        this.doublesA = realDoubles;
        this.doublesB = null;
    }

    public Doubles_Entity getDoublesA() { return doublesA; }
    public Doubles_Entity getDoublesB() { return doublesB; }

    public Doubles_Entity getWinner() { return winner; }
    public void setWinner(Doubles_Entity winner) { this.winner = winner; }

    public Doubles_Entity getLoser() { return loser; }
    public void setLoser(Doubles_Entity loser) { this.loser = loser; }
}