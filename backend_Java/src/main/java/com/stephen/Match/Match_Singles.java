package com.stephen.Match;

import com.stephen.Player.Player_Entity;
import com.stephen.Team.Team_Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "matches_singles")
@DiscriminatorValue("SINGLES")
public class Match_Singles extends Match_Entity {

    @ManyToOne
    @JoinColumn(name = "player_a_id", nullable = false)
    private Player_Entity playerA;

    @ManyToOne
    @JoinColumn(name = "player_b_id", nullable = false)
    private Player_Entity playerB;

    @ManyToOne
    @JoinColumn(name = "team_a_id")
    private Team_Entity teamA;

    @ManyToOne
    @JoinColumn(name = "team_b_id")
    private Team_Entity teamB;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Player_Entity winner;

    @ManyToOne
    @JoinColumn(name = "loser_id")
    private Player_Entity loser;

    protected Match_Singles() {}

    public Match_Singles(Player_Entity playerA, Player_Entity playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
    }

    public Match_Singles(Player_Entity playerA, Player_Entity playerB, Team_Entity teamA, Team_Entity teamB) {
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
    public Match_Singles(Player_Entity realPlayer) {
        this.playerA = realPlayer;
        this.playerB = null;
    }

    public boolean hasTeamContext() { return teamA != null && teamB != null; }

    public Player_Entity getPlayerA() { return playerA; }
    public Player_Entity getPlayerB() { return playerB; }

    public Team_Entity getTeamA() { return teamA; }
    public Team_Entity getTeamB() { return teamB; }

    public Player_Entity getWinner() { return winner; }
    public void setWinner(Player_Entity winner) { this.winner = winner; }

    public Player_Entity getLoser() { return loser; }
    public void setLoser(Player_Entity loser) { this.loser = loser; }
}