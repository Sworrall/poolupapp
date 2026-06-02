package com.stephen.Match;

import org.springframework.context.ApplicationEvent;

public class Match_UpdateEvent extends ApplicationEvent {

    private final Long matchId;

    public Match_UpdateEvent(Object source, Long matchId) {
        super(source);
        this.matchId = matchId;
    }

    public Long getMatchId() { return matchId; }
}