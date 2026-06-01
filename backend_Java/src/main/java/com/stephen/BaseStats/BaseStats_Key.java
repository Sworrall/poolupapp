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
    private Long eventID;

    @Column(name = "team_id")
    private Long teamID;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BaseStats_Key that = (BaseStats_Key) o;
        return Objects.equals(getEventID(), that.getEventID()) && Objects.equals(getTeamID(), that.getTeamID());    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventID(), getTeamID());
    }

    @Transient
    private static final Logger log = LoggerFactory.getLogger(BaseStats_Key.class);

    public BaseStats_Key(Long eventID) {
        this(eventID, null);
        log.info("BaseStats_Key created with null teamID for eventID: {}", eventID);
    }
    
    public BaseStats_Key(Long eventID, Long teamID) {
        this.eventID = eventID;
        this.teamID = teamID;
        log.info("BaseStats_Key created with eventID: {} and teamID: {}", eventID, teamID);
    }

    public Long getEventID() {
        return eventID;
    }

    public Long getTeamID() {
        return teamID;
    }

    public BaseStats_Key() {}
}