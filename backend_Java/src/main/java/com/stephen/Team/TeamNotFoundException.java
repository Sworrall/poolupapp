package com.stephen.Team;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(Long Id) {
        super("Team not found: " + Id);
    }
}