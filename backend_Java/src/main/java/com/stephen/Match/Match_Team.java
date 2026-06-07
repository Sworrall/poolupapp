package com.stephen.Match;

import com.stephen.Team.Team_Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "matches_team")
@DiscriminatorValue("TEAM")
public class Match_Team extends Match_Entity {

    @ManyToOne
    @JoinColumn(name = "team_a_id", nullable = false)
    private Team_Entity teamA;

    @ManyToOne
    @JoinColumn(name = "team_b_id", nullable = false)
    private Team_Entity teamB;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Team_Entity winner;

    @ManyToOne
    @JoinColumn(name = "loser_id")
    private Team_Entity loser;

    protected Match_Team() {}

    public Match_Team(Team_Entity teamA, Team_Entity teamB) {
        this.teamA = teamA;
        this.teamB = teamB;
    }

    /**
     * Bye constructor — used by Tournament_Service when one party slot is a bye.
     * The real team is stored as teamA; teamB is left null.
     * setBye(true) is called by the service after construction.
     */
    public Match_Team(Team_Entity realTeam) {
        this.teamA = realTeam;
        this.teamB = null;
    }

    public Team_Entity getTeamA() { return teamA; }
    public Team_Entity getTeamB() { return teamB; }

    public Team_Entity getWinner() { return winner; }
    public void setWinner(Team_Entity winner) { this.winner = winner; }

    public Team_Entity getLoser() { return loser; }
    public void setLoser(Team_Entity loser) { this.loser = loser; }
}