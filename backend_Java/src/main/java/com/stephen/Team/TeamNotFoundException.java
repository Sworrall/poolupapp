package com.stephen.Team;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(Long id) {
        super("Team not found: " + id);
    }
}