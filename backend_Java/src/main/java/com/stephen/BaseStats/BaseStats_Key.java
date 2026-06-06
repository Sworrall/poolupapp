package com.stephen.BaseStats;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Embeddable
public class BaseStats_Key {

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "team_id")
    private Long teamId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseStats_Key that = (BaseStats_Key) o;
        return Objects.equals(getEventId(), that.getEventId()) && Objects.equals(getTeamId(), that.getTeamId());    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventId(), getTeamId());
    }

    private static final Logger log = LoggerFactory.getLogger(BaseStats_Key.class);

    public BaseStats_Key(Long eventId) {
        this(eventId, null);
        log.info("BaseStats_Key created with null teamId for eventId: {}", eventId);
    }
    
    public BaseStats_Key(Long eventId, Long teamId) {
        this.eventId = eventId;
        this.teamId = teamId;
        log.info("BaseStats_Key created with eventId: {} and teamId: {}", eventId, teamId);
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public BaseStats_Key() {}
}