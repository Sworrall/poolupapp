package com.stephen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record BaseStats_Key(int eventID, Integer teamID) {

    private static final Logger log = LoggerFactory.getLogger(BaseStats_Key.class);

    public BaseStats_Key(int eventID) {
        this(eventID, null);
        log.info("BaseStats_Key created with null teamID for eventID: " + eventID);
    }
    
    public BaseStats_Key(int eventID, Integer teamID) {
        this.eventID = eventID;
        this.teamID = teamID;
        log.info("BaseStats_Key created with eventID: " + eventID + " and teamID: " + teamID);
    }
}