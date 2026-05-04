package com.stephen.Stats;

import com.stephen.Frame.Frame;
import com.stephen.Match.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseStats_Service <S extends StatHolder<S>>{
    private static final int GLOBAL = 0;
    private static final Logger log = LoggerFactory.getLogger(BaseStats_Service.class);


    // --- GETTERS ---
    public int getStats(S s, int eventID, StatField field) {
        log.info("Getting Stats for EventID: {} and Field: {}", eventID, field);
        return s.getOrCreateStats(new BaseStats_Key(eventID, s.getID())).get(field);
    }


    // --- MISC ---
    public static void applyEvent(BaseStats_Key eventKey, StatField field, StatHolder<?> holder) {
        holder.getOrCreateStats(eventKey).increment(field);
        BaseStats_Key K = new BaseStats_Key(eventKey.eventID(), holder.getID());
        holder.getOrCreateStats(K).increment(field);
        log.info("Applied Event: {} Field: {} to Holder: {}", eventKey, field, holder.getID());
    }

    public static void applyGLOBAL(BaseStats_Key eventKey, StatField field, StatHolder<?> holder) {
        holder.getOrCreateStats(eventKey).increment(field);
        BaseStats_Key K = new BaseStats_Key(GLOBAL, holder.getID());
        holder.getOrCreateStats(K).increment(field);
        log.info("Applied GLOBAL Event: {} Field: {} to Holder: {}", eventKey, field, holder.getID());
    }

    public static<S extends StatHolder<S>> void applyFrame_WIN_LOSS(BaseStats_Key partyAKey, BaseStats_Key partyBKey, Frame<S> f){
        S sh1 = f.getParty1();
        S sh2 = f.getParty2();
        if (f.isPlayed()) {
            if (f.getWinner().equals(sh1)) {
                BaseStats_Service.applyEvent(partyAKey, StatField.FRAME_WIN, sh1);
                BaseStats_Service.applyEvent(partyBKey, StatField.FRAME_LOSS, sh2);
                if (f.isBreakDish()) {
                    BaseStats_Service.applyEvent(partyAKey, StatField.FRAME_BREAK_DISH, sh1);
                    log.info("Applied BREAK_DISH Event to Holder: {}", sh1.getID());
                }
            }else{
                BaseStats_Service.applyEvent(partyAKey, StatField.FRAME_LOSS, sh1);
                BaseStats_Service.applyEvent(partyBKey, StatField.FRAME_WIN, sh2);
                if (f.isBreakDish()) {
                    BaseStats_Service.applyEvent(partyBKey, StatField.FRAME_BREAK_DISH, sh2);
                    log.info("Applied BREAK_DISH Event to Holder: {}", sh2.getID());
                }
            }
        }else{
            log.error("Attempted to apply WIN_LOSS for a frame that has not been played. Frame: {}", f);
            throw new IllegalArgumentException("Frame Not Played");
        }
        log.info("Applied WIN_LOSS Event for Frame: {} between Holders: {} and {}", f, sh1.getID(), sh2.getID());
    }

    public void applyMatch_WIN_LOSS(BaseStats_Key partyAKey, BaseStats_Key partyBKey, Match<S> m){
        S h1 = m.getParty1();
        S h2 = m.getParty2();
        if (m.isPlayed()) {
            if(m.isDraw()){
                BaseStats_Service.applyEvent(partyAKey, StatField.MATCH_DRAW, h1);
                BaseStats_Service.applyEvent(partyBKey, StatField.MATCH_DRAW, h2);
            }
            if (m.getWinner().equals(h1)) {
                BaseStats_Service.applyEvent(partyAKey, StatField.MATCH_WIN, h1);
                BaseStats_Service.applyEvent(partyBKey, StatField.MATCH_LOSS, h2);
            }else{
                BaseStats_Service.applyEvent(partyAKey, StatField.MATCH_LOSS, h1);
                BaseStats_Service.applyEvent(partyBKey, StatField.MATCH_WIN, h2);
            }
        }else{
            log.error("Attempted to apply WIN_LOSS for a match that has not been played. Match: {}", m);
            throw new IllegalArgumentException("Match Not Played");
        }
        log.info("Applied WIN_LOSS Event for Match: {} between Holders: {} and {}", m, h1.getID(), h2.getID());
    }
}