package com.stephen.Tournament;

public class TournamentNotFoundException extends RuntimeException {

    public TournamentNotFoundException(Long id) {
        super("Tournament not found: " + id);
    }
}
