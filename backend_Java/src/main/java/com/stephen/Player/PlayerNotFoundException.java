package com.stephen.Player;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(Long ID) {
        super("Player not found: " + ID);
    }
}