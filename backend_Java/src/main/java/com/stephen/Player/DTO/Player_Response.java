package com.stephen.Player.DTO;

import java.util.List;

public class Player_Response {

    private List<Player_DTO> players;

    public Player_Response(List<Player_DTO> players) {
        this.players = players;
    }

    public List<Player_DTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player_DTO> players) {
        this.players = players;
    }
}