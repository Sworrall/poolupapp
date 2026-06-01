package com.stephen.Match.Team;

import com.stephen.Match.Match;
import com.stephen.Team.Team;
import jakarta.persistence.*;

@Entity
@Table(name = "matches_team")
@DiscriminatorValue("TEAM")
public class Match_Team extends Match {

    @ManyToOne
    @JoinColumn(name = "team_a_id", nullable = false)
    private Team teamA;

    @ManyToOne
    @JoinColumn(name = "team_b_id", nullable = false)
    private Team teamB;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Team winner;

    @ManyToOne
    @JoinColumn(name = "loser_id")
    private Team loser;

    protected Match_Team() {}

    public Match_Team(Team teamA, Team teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
    }

    /**
     * Bye constructor — used by Tournament_Service when one party slot is a bye.
     * The real team is stored as teamA; teamB is left null.
     * setBye(true) is called by the service after construction.
     */
    public Match_Team(Team realTeam) {
        this.teamA = realTeam;
        this.teamB = null;
    }

    public Team getTeamA() { return teamA; }
    public Team getTeamB() { return teamB; }

    public Team getWinner() { return winner; }
    public void setWinner(Team winner) { this.winner = winner; }

    public Team getLoser() { return loser; }
    public void setLoser(Team loser) { this.loser = loser; }
}