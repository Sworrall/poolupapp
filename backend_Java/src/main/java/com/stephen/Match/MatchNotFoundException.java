package com.stephen.Match;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(Long Id) {
        super("Match not found: " + Id);
    }
}