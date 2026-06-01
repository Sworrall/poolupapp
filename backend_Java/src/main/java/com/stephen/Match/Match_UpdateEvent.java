package com.stephen.Match;

import org.springframework.context.ApplicationEvent;

public class Match_UpdateEvent extends ApplicationEvent {

    private final Long matchID;

    public Match_UpdateEvent(Object source, Long matchID) {
        super(source);
        this.matchID = matchID;
    }

    public Long getMatchID() { return matchID; }
}