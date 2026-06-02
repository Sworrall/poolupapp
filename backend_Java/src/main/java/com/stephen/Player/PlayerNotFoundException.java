package com.stephen.Player;

public class PlayerNotFoundException extends RuntimeException {
    public PlayerNotFoundException(Long Id) {
        super("Player not found: " + Id);
    }
}