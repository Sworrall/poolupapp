package com.stephen.Match;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class Match_EventPublisher {

    private final ApplicationEventPublisher publisher;

    public Match_EventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishMatchUpdate(Long id) {
        publisher.publishEvent(new Match_UpdateEvent(this, id));
    }
}