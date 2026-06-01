package com.stephen.BaseStats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BaseStats_Repository extends JpaRepository<StatEntry, Long> {

    /**
     * All stat rows for a party across all scopes.
     * Used to build a full career stats picture.
     */
    List<StatEntry> findByHolderIDAndHolderType(Long holderID, HolderType holderType);

    /**
     * All stat rows for a party at a specific scope (e.g. all FRAME rows).
     */
    List<StatEntry> findByHolderIDAndHolderTypeAndEventScope(
            Long holderID, HolderType holderType, StatScope eventScope);

    /**
     * Exact stat row lookup — one row per unique context.
     */
    Optional<StatEntry> findByHolderIDAndHolderTypeAndEventIDAndEventScopeAndTeamID(
            Long holderID, HolderType holderType,
            Long eventID, StatScope eventScope, Long teamID);

    /**
     * All stat rows scoped to a specific tournament (includes frame and match rows).
     * Used for tournament leaderboard construction.
     */
    List<StatEntry> findByTournamentIDAndHolderType(Long tournamentID, HolderType holderType);

    /**
     * All match-scoped rows for a specific tournament.
     */
    List<StatEntry> findByTournamentIDAndHolderTypeAndEventScope(
            Long tournamentID, HolderType holderType, StatScope eventScope);

    /**
     * All frame-scoped rows for a specific match.
     */
    List<StatEntry> findByMatchIDAndHolderType(Long matchID, HolderType holderType);

    /**
     * Global stats (eventScope = GLOBAL) for all parties of a given type.
     * Used for overall career rankings.
     */
    @Query("SELECT s FROM StatEntry s WHERE s.eventScope = 'GLOBAL' AND s.holderType = :holderType")
    List<StatEntry> findGlobalStatsByHolderType(@Param("holderType") HolderType holderType);
}
