package com.stephen.Team;

public class TeamNotFoundException extends RuntimeException {
    public TeamNotFoundException(Long ID) {
        super("Team not found: " + ID);
    }
}