package com.stephen.BaseStats;

import com.stephen.Frame.Doubles.Frame_Doubles;
import com.stephen.Frame.Frame;
import com.stephen.Frame.Singles.Frame_Singles;
import com.stephen.Match.Doubles.Match_Doubles;
import com.stephen.Match.Match;
import com.stephen.Match.Singles.Match_Singles;
import com.stephen.Match.Team.Match_Team;
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
     *
     * Every stat increment touches two rows: the event-scoped row and the global row.
     */
    @Transactional
    public void applyEvent(Long holderID, HolderType holderType,
                           Long eventID, StatScope eventScope,
                           Long matchID, Long tournamentID, Long teamID,
                           StatField field) {
        // Event-scoped stat row
        StatEntry eventStat = getOrCreate(
                holderID, holderType, eventID, eventScope, matchID, tournamentID, teamID);
        eventStat.increment(field);
        statsRepo.save(eventStat);

        // Global stat row — always eventID=0, GLOBAL scope, no match/tournament context
        StatEntry globalStat = getOrCreate(
                holderID, holderType, GLOBAL_EVENT_ID, StatScope.GLOBAL, null, null, teamID);
        globalStat.increment(field);
        statsRepo.save(globalStat);

        log.info("Applied {} to holder {} ({}) scope={} event={}",
                field, holderID, holderType, eventScope, eventID);
    }

    /**
     * Gets the current value of a StatField for a party in a specific event context.
     */
    public int getStats(Long holderID, HolderType holderType,
                        Long eventID, StatScope eventScope,
                        Long teamID, StatField field) {
        return statsRepo.findByHolderIDAndHolderTypeAndEventIDAndEventScopeAndTeamID(
                        holderID, holderType, eventID, eventScope, teamID)
                .map(e -> e.get(field))
                .orElse(0);
    }

    /**
     * Returns the full StatEntry for a party in a specific context.
     * Returns an unsaved zero-stat entry if none exists yet.
     */
    public StatEntry getStatEntry(Long holderID, HolderType holderType,
                                  Long eventID, StatScope eventScope,
                                  Long matchID, Long tournamentID, Long teamID) {
        return getOrCreate(holderID, holderType, eventID, eventScope, matchID, tournamentID, teamID);
    }


    // -------------------------------------------------------------------------
    // FRAME STATS
    // -------------------------------------------------------------------------

    /**
     * Applies FRAME_WIN / FRAME_LOSS / FRAME_TOTAL (and FRAME_BREAK_DISH if set)
     * to both parties in a played frame.
     *
     * Each party gets four rows updated:
     *   - FRAME-scoped row  (eventID=frameID,  matchID, tournamentID)
     *   - GLOBAL row        (eventID=0)
     *
     * @param frame        the played frame
     * @param matchID      parent match ID
     * @param tournamentID parent tournament ID — null for standalone matches
     * @param teamID       team context — null for standalone events
     */
    @Transactional
    public void applyFrameResult(Frame frame, Long matchID, Long tournamentID, Long teamID) {
        if (!frame.isPlayed()) {
            log.error("Attempted to apply frame result for unplayed frame: {}", frame.getID());
            throw new IllegalArgumentException("Frame not played: " + frame.getID());
        }

        switch (frame) {
            case Frame_Singles f -> applyFrameSingles(f, matchID, tournamentID, teamID);
            case Frame_Doubles f -> applyFrameDoubles(f, matchID, tournamentID, teamID);
            default -> throw new IllegalArgumentException(
                    "Unsupported frame type: " + frame.getClass().getSimpleName());
        }
    }

    private void applyFrameSingles(Frame_Singles frame, Long matchID,
                                   Long tournamentID, Long teamID) {
        Long frameID  = frame.getID();
        Long winnerID = frame.getWinner().getID();
        Long loserID  = frame.getLoser().getID();

        applyEvent(winnerID, HolderType.SINGLES, frameID, StatScope.FRAME,
                matchID, tournamentID, teamID, StatField.FRAME_WIN);
        applyEvent(loserID,  HolderType.SINGLES, frameID, StatScope.FRAME,
                matchID, tournamentID, teamID, StatField.FRAME_LOSS);
        applyEvent(winnerID, HolderType.SINGLES, frameID, StatScope.FRAME,
                matchID, tournamentID, teamID, StatField.FRAME_TOTAL);
        applyEvent(loserID,  HolderType.SINGLES, frameID, StatScope.FRAME,
                matchID, tournamentID, teamID, StatField.FRAME_TOTAL);

        if (frame.isBreakDish()) {
            applyEvent(winnerID, HolderType.SINGLES, frameID, StatScope.FRAME,
                    matchID, tournamentID, teamID, StatField.FRAME_BREAK_DISH);
        }

        log.info("Applied singles frame result: winner={}, loser={}, frame={}",
                winnerID, loserID, frameID);
    }

    private void applyFrameDoubles(Frame_Doubles frame, Long matchID,
                                   Long tournamentID, Long teamID) {
        Long frameID  = frame.getID();
        Long winnerID = frame.getWinner().getID();
        Long loserID  = frame.getDoublesA().getID().equals(winnerID)
                ? frame.getDoublesB().getID()
                : frame.getDoublesA().getID();

        applyEvent(winnerID, HolderType.DOUBLES, frameID, StatScope.FRAME,
                matchID, tournamentID, teamID, StatField.FRAME_WIN);
        applyEvent(loserID,  HolderType.DOUBLES, frameID, StatScope.FRAME,
                matchID, tournamentID, teamID, StatField.FRAME_LOSS);
        applyEvent(winnerID, HolderType.DOUBLES, frameID, StatScope.FRAME,
                matchID, tournamentID, teamID, StatField.FRAME_TOTAL);
        applyEvent(loserID,  HolderType.DOUBLES, frameID, StatScope.FRAME,
                matchID, tournamentID, teamID, StatField.FRAME_TOTAL);

        if (frame.isBreakDish()) {
            applyEvent(winnerID, HolderType.DOUBLES, frameID, StatScope.FRAME,
                    matchID, tournamentID, teamID, StatField.FRAME_BREAK_DISH);
        }

        log.info("Applied doubles frame result: winner={}, loser={}, frame={}",
                winnerID, loserID, frameID);
    }


    // -------------------------------------------------------------------------
    // MATCH STATS
    // -------------------------------------------------------------------------

    /**
     * Applies MATCH_WIN / MATCH_LOSS / MATCH_DRAW / MATCH_TOTAL to both parties.
     *
     * Each party gets two rows updated:
     *   - MATCH-scoped row  (eventID=matchID, tournamentID)
     *   - GLOBAL row        (eventID=0)
     *
     * @param match        the played match
     * @param tournamentID parent tournament ID — null for standalone matches
     * @param teamID       team context — null for standalone events
     */
    @Transactional
    public void applyMatchResult(Match match, Long tournamentID, Long teamID) {
        if (!match.isPlayed()) {
            log.error("Attempted to apply match result for unplayed match: {}", match.getID());
            throw new IllegalArgumentException("Match not played: " + match.getID());
        }

        switch (match) {
            case Match_Singles m -> applyMatchSingles(m, tournamentID, teamID);
            case Match_Doubles m -> applyMatchDoubles(m, tournamentID, teamID);
            case Match_Team    m -> applyMatchTeam(m, tournamentID, teamID);
            default -> throw new IllegalArgumentException(
                    "Unsupported match type: " + match.getClass().getSimpleName());
        }
    }

    private void applyMatchSingles(Match_Singles match, Long tournamentID, Long teamID) {
        Long matchID = match.getID();
        Long p1ID    = match.getPlayerA().getID();
        Long p2ID    = match.getPlayerB().getID();

        applyEvent(p1ID, HolderType.SINGLES, matchID, StatScope.MATCH,
                null, tournamentID, teamID, StatField.MATCH_TOTAL);
        applyEvent(p2ID, HolderType.SINGLES, matchID, StatScope.MATCH,
                null, tournamentID, teamID, StatField.MATCH_TOTAL);

        if (match.isDraw()) {
            applyEvent(p1ID, HolderType.SINGLES, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_DRAW);
            applyEvent(p2ID, HolderType.SINGLES, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_DRAW);
        } else {
            Long winnerID = match.getWinner().getID();
            Long loserID  = match.getLoser().getID();
            applyEvent(winnerID, HolderType.SINGLES, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_WIN);
            applyEvent(loserID,  HolderType.SINGLES, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_LOSS);
        }

        log.info("Applied singles match result for match {}", matchID);
    }

    private void applyMatchDoubles(Match_Doubles match, Long tournamentID, Long teamID) {
        Long matchID = match.getID();
        Long d1ID    = match.getDoublesA().getID();
        Long d2ID    = match.getDoublesB().getID();

        applyEvent(d1ID, HolderType.DOUBLES, matchID, StatScope.MATCH,
                null, tournamentID, teamID, StatField.MATCH_TOTAL);
        applyEvent(d2ID, HolderType.DOUBLES, matchID, StatScope.MATCH,
                null, tournamentID, teamID, StatField.MATCH_TOTAL);

        if (match.isDraw()) {
            applyEvent(d1ID, HolderType.DOUBLES, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_DRAW);
            applyEvent(d2ID, HolderType.DOUBLES, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_DRAW);
        } else {
            Long winnerID = match.getWinner().getID();
            Long loserID  = match.getLoser().getID();
            applyEvent(winnerID, HolderType.DOUBLES, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_WIN);
            applyEvent(loserID,  HolderType.DOUBLES, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_LOSS);
        }

        log.info("Applied doubles match result for match {}", matchID);
    }

    private void applyMatchTeam(Match_Team match, Long tournamentID, Long teamID) {
        Long matchID = match.getID();
        Long t1ID    = match.getTeamA().getID();
        Long t2ID    = match.getTeamB().getID();

        applyEvent(t1ID, HolderType.TEAM, matchID, StatScope.MATCH,
                null, tournamentID, teamID, StatField.MATCH_TOTAL);
        applyEvent(t2ID, HolderType.TEAM, matchID, StatScope.MATCH,
                null, tournamentID, teamID, StatField.MATCH_TOTAL);

        if (match.isDraw()) {
            applyEvent(t1ID, HolderType.TEAM, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_DRAW);
            applyEvent(t2ID, HolderType.TEAM, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_DRAW);
        } else {
            Long winnerID = match.getWinner().getID();
            Long loserID  = match.getLoser().getID();
            applyEvent(winnerID, HolderType.TEAM, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_WIN);
            applyEvent(loserID,  HolderType.TEAM, matchID, StatScope.MATCH,
                    null, tournamentID, teamID, StatField.MATCH_LOSS);
        }

        log.info("Applied team match result for match {}", matchID);
    }


    // -------------------------------------------------------------------------
    // INTERNAL HELPERS
    // -------------------------------------------------------------------------

    private StatEntry getOrCreate(Long holderID, HolderType holderType,
                                  Long eventID, StatScope eventScope,
                                  Long matchID, Long tournamentID, Long teamID) {
        return statsRepo.findByHolderIDAndHolderTypeAndEventIDAndEventScopeAndTeamID(
                        holderID, holderType, eventID, eventScope, teamID)
                .orElse(new StatEntry(holderID, holderType,
                        eventID, eventScope, matchID, tournamentID, teamID));
    }
}
