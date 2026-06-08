package com.stephen.BaseStats;

import com.stephen.Frame.Doubles.Frame_Doubles;
import com.stephen.Frame.Frame_Entity;
import com.stephen.Frame.Singles.Frame_Singles;
import com.stephen.Match.Match_Doubles;
import com.stephen.Match.Match_Entity;
import com.stephen.Match.Match_Singles;
import com.stephen.Match.Match_Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BaseStats_Service {

    private static final Long GLOBAL_EVENT_ID = 0L;
    private static final Logger log = LoggerFactory.getLogger(BaseStats_Service.class);

    private final BaseStats_Repository statsRepo;

    public BaseStats_Service(BaseStats_Repository statsRepo) {
        this.statsRepo = statsRepo;
    }


    // -------------------------------------------------------------------------
    // CORE API
    // -------------------------------------------------------------------------

    /**
     * Increments a StatField for a party in a specific event context,
     * and also increments it in the party's global row (eventScope = GLOBAL).

     * Every stat increment touches two rows: the event-scoped row and the global row.
     */
    @Transactional
    public void applyEvent(Long holderId, HolderType holderType,
                           Long eventId, StatScope eventScope,
                           Long matchId, Long tournamentId, Long teamId,
                           StatField field) {
        // Event-scoped stat row
        StatEntry eventStat = getOrCreate(
                holderId, holderType, eventId, eventScope, matchId, tournamentId, teamId);
        eventStat.increment(field);
        statsRepo.save(eventStat);

        // Global stat row — always eventId=0, GLOBAL scope, no match/tournament context
        StatEntry globalStat = getOrCreate(
                holderId, holderType, GLOBAL_EVENT_ID, StatScope.GLOBAL, null, null, teamId);
        globalStat.increment(field);
        statsRepo.save(globalStat);

        log.info("Applied {} to holder {} ({}) scope={} event={}",
                field, holderId, holderType, eventScope, eventId);
    }

    /**
     * Gets the current value of a StatField for a party in a specific event context.
     */
    public int getStats(Long holderId, HolderType holderType,
                        Long eventId, StatScope eventScope,
                        Long teamId, StatField field) {
        return statsRepo.findByHolderIdAndHolderTypeAndEventIdAndEventScopeAndTeamId(
                        holderId, holderType, eventId, eventScope, teamId)
                .map(e -> e.get(field))
                .orElse(0);
    }

    /**
     * Returns the full StatEntry for a party in a specific context.
     * Returns an unsaved zero-stat entry if none exists yet.
     */
    public StatEntry getStatEntry(Long holderId, HolderType holderType,
                                  Long eventId, StatScope eventScope,
                                  Long matchId, Long tournamentId, Long teamId) {
        return getOrCreate(holderId, holderType, eventId, eventScope, matchId, tournamentId, teamId);
    }


    // -------------------------------------------------------------------------
    // FRAME STATS
    // -------------------------------------------------------------------------

    /**
     * Applies FRAME_WIN / FRAME_LOSS / FRAME_TOTAL (and FRAME_BREAK_DISH if set)
     * to both parties in a played frame.

     * Each party gets four rows updated:
     *   - FRAME-scoped row  (eventId=frameId,  matchId, tournamentId)
     *   - GLOBAL row        (eventId=0)

     * @param frame        the played frame
     * @param matchId      parent match Id
     * @param tournamentId parent tournament Id — null for standalone matches
     * @param teamId       team context — null for standalone events
     */
    @Transactional
    public void applyFrameResult(Frame_Entity frame, Long matchId, Long tournamentId, Long teamId) {
        if (!frame.isPlayed()) {
            log.error("Attempted to apply frame result for unplayed frame: {}", frame.getId());
            throw new IllegalArgumentException("Frame not played: " + frame.getId());
        }

        switch (frame) {
            case Frame_Singles f -> applyFrameSingles(f, matchId, tournamentId, teamId);
            case Frame_Doubles f -> applyFrameDoubles(f, matchId, tournamentId, teamId);
            default -> throw new IllegalArgumentException(
                    "Unsupported frame type: " + frame.getClass().getSimpleName());
        }
    }

    private void applyFrameSingles(Frame_Singles frame, Long matchId,
                                   Long tournamentId, Long teamId) {
        Long frameId  = frame.getId();
        Long winnerId = frame.getWinner().getId();
        Long loserId  = frame.getLoser().getId();

        applyEvent(winnerId, HolderType.SINGLES, frameId, StatScope.FRAME,
                matchId, tournamentId, teamId, StatField.FRAME_WIN);
        applyEvent(loserId,  HolderType.SINGLES, frameId, StatScope.FRAME,
                matchId, tournamentId, teamId, StatField.FRAME_LOSS);
        applyEvent(winnerId, HolderType.SINGLES, frameId, StatScope.FRAME,
                matchId, tournamentId, teamId, StatField.FRAME_TOTAL);
        applyEvent(loserId,  HolderType.SINGLES, frameId, StatScope.FRAME,
                matchId, tournamentId, teamId, StatField.FRAME_TOTAL);

        if (frame.isBreakDish()) {
            applyEvent(winnerId, HolderType.SINGLES, frameId, StatScope.FRAME,
                    matchId, tournamentId, teamId, StatField.FRAME_BREAK_DISH);
        }

        log.info("Applied singles frame result: winner={}, loser={}, frame={}",
                winnerId, loserId, frameId);
    }

    private void applyFrameDoubles(Frame_Doubles frame, Long matchId,
                                   Long tournamentId, Long teamId) {
        Long frameId  = frame.getId();
        Long winnerId = frame.getWinner().getId();
        Long loserId  = frame.getDoublesA().getId().equals(winnerId)
                ? frame.getDoublesB().getId()
                : frame.getDoublesA().getId();

        applyEvent(winnerId, HolderType.DOUBLES, frameId, StatScope.FRAME,
                matchId, tournamentId, teamId, StatField.FRAME_WIN);
        applyEvent(loserId,  HolderType.DOUBLES, frameId, StatScope.FRAME,
                matchId, tournamentId, teamId, StatField.FRAME_LOSS);
        applyEvent(winnerId, HolderType.DOUBLES, frameId, StatScope.FRAME,
                matchId, tournamentId, teamId, StatField.FRAME_TOTAL);
        applyEvent(loserId,  HolderType.DOUBLES, frameId, StatScope.FRAME,
                matchId, tournamentId, teamId, StatField.FRAME_TOTAL);

        if (frame.isBreakDish()) {
            applyEvent(winnerId, HolderType.DOUBLES, frameId, StatScope.FRAME,
                    matchId, tournamentId, teamId, StatField.FRAME_BREAK_DISH);
        }

        log.info("Applied doubles frame result: winner={}, loser={}, frame={}",
                winnerId, loserId, frameId);
    }


    // -------------------------------------------------------------------------
    // MATCH STATS
    // -------------------------------------------------------------------------

    /**
     * Applies MATCH_WIN / MATCH_LOSS / MATCH_DRAW / MATCH_TOTAL to both parties.

     * Each party gets two rows updated:
     *   - MATCH-scoped row  (eventId=matchId, tournamentId)
     *   - GLOBAL row        (eventId=0)

     * @param match        the played match
     * @param tournamentId parent tournament Id — null for standalone matches
     * @param teamId       team context — null for standalone events
     */
    @Transactional
    public void applyMatchResult(Match_Entity match, Long tournamentId, Long teamId) {
        if (!match.isPlayed()) {
            log.error("Attempted to apply match result for unplayed match: {}", match.getId());
            throw new IllegalArgumentException("Match not played: " + match.getId());
        }

        switch (match) {
            case Match_Singles m -> applyMatchSingles(m, tournamentId, teamId);
            case Match_Doubles m -> applyMatchDoubles(m, tournamentId, teamId);
            case Match_Team    m -> applyMatchTeam(m, tournamentId, teamId);
            default -> throw new IllegalArgumentException(
                    "Unsupported match type: " + match.getClass().getSimpleName());
        }
    }

    private void applyMatchSingles(Match_Singles match, Long tournamentId, Long teamId) {
        Long matchId = match.getId();
        Long p1Id    = match.getPlayerA().getId();
        Long p2Id    = match.getPlayerB().getId();

        applyEvent(p1Id, HolderType.SINGLES, matchId, StatScope.MATCH,
                null, tournamentId, teamId, StatField.MATCH_TOTAL);
        applyEvent(p2Id, HolderType.SINGLES, matchId, StatScope.MATCH,
                null, tournamentId, teamId, StatField.MATCH_TOTAL);

        if (match.isDraw()) {
            applyEvent(p1Id, HolderType.SINGLES, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_DRAW);
            applyEvent(p2Id, HolderType.SINGLES, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_DRAW);
        } else {
            Long winnerId = match.getWinner().getId();
            Long loserId  = match.getLoser().getId();
            applyEvent(winnerId, HolderType.SINGLES, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_WIN);
            applyEvent(loserId,  HolderType.SINGLES, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_LOSS);
        }

        log.info("Applied singles match result for match {}", matchId);
    }

    private void applyMatchDoubles(Match_Doubles match, Long tournamentId, Long teamId) {
        Long matchId = match.getId();
        Long d1Id    = match.getDoublesA().getId();
        Long d2Id    = match.getDoublesB().getId();

        applyEvent(d1Id, HolderType.DOUBLES, matchId, StatScope.MATCH,
                null, tournamentId, teamId, StatField.MATCH_TOTAL);
        applyEvent(d2Id, HolderType.DOUBLES, matchId, StatScope.MATCH,
                null, tournamentId, teamId, StatField.MATCH_TOTAL);

        if (match.isDraw()) {
            applyEvent(d1Id, HolderType.DOUBLES, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_DRAW);
            applyEvent(d2Id, HolderType.DOUBLES, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_DRAW);
        } else {
            Long winnerId = match.getWinner().getId();
            Long loserId  = match.getLoser().getId();
            applyEvent(winnerId, HolderType.DOUBLES, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_WIN);
            applyEvent(loserId,  HolderType.DOUBLES, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_LOSS);
        }

        log.info("Applied doubles match result for match {}", matchId);
    }

    private void applyMatchTeam(Match_Team match, Long tournamentId, Long teamId) {
        Long matchId = match.getId();
        Long t1Id    = match.getTeamA().getId();
        Long t2Id    = match.getTeamB().getId();

        applyEvent(t1Id, HolderType.TEAM, matchId, StatScope.MATCH,
                null, tournamentId, teamId, StatField.MATCH_TOTAL);
        applyEvent(t2Id, HolderType.TEAM, matchId, StatScope.MATCH,
                null, tournamentId, teamId, StatField.MATCH_TOTAL);

        if (match.isDraw()) {
            applyEvent(t1Id, HolderType.TEAM, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_DRAW);
            applyEvent(t2Id, HolderType.TEAM, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_DRAW);
        } else {
            Long winnerId = match.getWinner().getId();
            Long loserId  = match.getLoser().getId();
            applyEvent(winnerId, HolderType.TEAM, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_WIN);
            applyEvent(loserId,  HolderType.TEAM, matchId, StatScope.MATCH,
                    null, tournamentId, teamId, StatField.MATCH_LOSS);
        }
        log.info("Applied team match result for match {}", matchId);
    }


    // -------------------------------------------------------------------------
    // INTERNAL HELPERS
    // -------------------------------------------------------------------------

    private StatEntry getOrCreate(Long holderId, HolderType holderType,
                                  Long eventId, StatScope eventScope,
                                  Long matchId, Long tournamentId, Long teamId) {
        return statsRepo.findByHolderIdAndHolderTypeAndEventIdAndEventScopeAndTeamId(
                        holderId, holderType, eventId, eventScope, teamId)
                .orElse(new StatEntry(holderId, holderType,
                        eventId, eventScope, matchId, tournamentId, teamId));
    }
}
