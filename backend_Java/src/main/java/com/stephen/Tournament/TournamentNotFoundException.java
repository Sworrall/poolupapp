package com.stephen.Tournament;

public class TournamentNotFoundException extends RuntimeException {

    public TournamentNotFoundException(Long Id) {
        super("Tournament not found: " + Id);
    }
}
