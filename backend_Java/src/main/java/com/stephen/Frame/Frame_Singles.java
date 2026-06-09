package com.stephen.Frame;

import com.stephen.Player.Player_Entity;
import com.stephen.Team.Team_Entity;
import jakarta.persistence.*;

@Entity
@Table(name = "frames_singles")
@DiscriminatorValue("SINGLES")
public class Frame_Singles extends Frame_Entity {

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

    protected Frame_Singles() {}

    public Frame_Singles(Player_Entity playerA, Player_Entity playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
    }

    public Frame_Singles(Player_Entity playerA, Player_Entity playerB, Team_Entity teamA, Team_Entity teamB) {
        this.playerA = playerA;
        this.playerB = playerB;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public boolean hasTeamContext() {
        return teamA != null && teamB != null;
    }

    public Player_Entity getPlayerA() { return playerA; }
    public Player_Entity getPlayerB() { return playerB; }

    public Team_Entity getTeamA() { return teamA; }
    public Team_Entity getTeamB() { return teamB; }

    public Player_Entity getWinner() { return winner; }
    public void setWinner(Player_Entity winner) { this.winner = winner; }

    public Player_Entity getLoser() { return loser; }
    public void setLoser(Player_Entity loser) { this.loser = loser; }
}