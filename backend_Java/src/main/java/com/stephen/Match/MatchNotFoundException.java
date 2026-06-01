package com.stephen.Match;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(Long ID) {
        super("Match not found: " + ID);
    }
}