package com.stephen.Tournament;

public class TournamentNotFoundException extends RuntimeException {

    public TournamentNotFoundException(Long ID) {
        super("Tournament not found: " + ID);
    }
}
