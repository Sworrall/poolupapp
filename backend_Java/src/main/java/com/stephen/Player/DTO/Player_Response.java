package com.stephen.Player.DTO;


import com.stephen.Player.Player_Entity;

import java.util.List;

public class Player_Response {

    private List<Player_Entity> players;

    public Player_Response(List<Player_Entity> players) {
        this.players = players;
    }

    public List<Player_Entity> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player_Entity> players) {
        this.players = players;
    }
}