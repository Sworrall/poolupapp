package com.stephen.Match;

public class MatchNotFoundException extends RuntimeException {
    public MatchNotFoundException(Long id) {
        super("Match not found: " + id);
    }
}