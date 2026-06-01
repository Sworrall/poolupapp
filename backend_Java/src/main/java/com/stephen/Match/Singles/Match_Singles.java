package com.stephen.Match.Singles;

import com.stephen.Match.Match;
import com.stephen.Player.Player;
import com.stephen.Team.Team;
import jakarta.persistence.*;

@Entity
@Table(name = "matches_singles")
@DiscriminatorValue("SINGLES")
public class Match_Singles extends Match {

    @ManyToOne
    @JoinColumn(name = "player_a_id", nullable = false)
    private Player playerA;

    @ManyToOne
    @JoinColumn(name = "player_b_id", nullable = false)
    private Player playerB;

    @ManyToOne
    @JoinColumn(name = "team_a_id")
    private Team teamA;

    @ManyToOne
    @JoinColumn(name = "team_b_id")
    private Team teamB;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Player winner;

    @ManyToOne
    @JoinColumn(name = "loser_id")
    private Player loser;

    protected Match_Singles() {}

    public Match_Singles(Player playerA, Player playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
    }

    public Match_Singles(Player playerA, Player playerB, Team teamA, Team teamB) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    /**
     * Bye constructor — used by Tournament_Service when one party slot is a bye.
     * The real player is stored as playerA; playerB is left null.
     * setBye(true) is called by the service after construction.
     */
    public Match_Singles(Player realPlayer) {
        this.playerA = realPlayer;
        this.playerB = null;
    }

    public boolean hasTeamContext() { return teamA != null && teamB != null; }

    public Player getPlayerA() { return playerA; }
    public Player getPlayerB() { return playerB; }

    public Team getTeamA() { return teamA; }
    public Team getTeamB() { return teamB; }

    public Player getWinner() { return winner; }
    public void setWinner(Player winner) { this.winner = winner; }

    public Player getLoser() { return loser; }
    public void setLoser(Player loser) { this.loser = loser; }
}