package com.stephen.Tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Tournament_Match_Repository extends JpaRepository<Tournament_Match, Long> {

    /**
     * All fixtures for a tournament, ordered for deterministic reconstruction
     * of the original 2D matchList structure:
     *   - outer list index = roundNumber  (KO round / GroupStage group / 0 for flat formats)
     *   - inner list index = sequence
     */
    List<Tournament_Match> findByTournamentIdOrderByRoundNumberAscSequenceAsc(Long id);

    /**
     * All fixtures for a specific round/group within a tournament.
     * Used by KO service to retrieve only the current round's matches.
     */
    List<Tournament_Match> findByTournamentIdAndRoundNumberOrderBySequenceAsc(Long id, int roundNumber);

    /**
     * Highest round number recorded for a tournament.
     * Used by KO service to determine the current round index without loading all fixtures.
     */
    @Query("SELECT MAX(tm.roundNumber) FROM TournamentMatch tm WHERE tm.tournament.id = :tournamentId")
    Integer findMaxRoundNumber(@Param("tournamentId") Long tournamentId);

    /**
     * Count of fixtures in a given round. Used to validate round completeness.
     */
    long countByTournamentIdAndRoundNumber(Long id, int roundNumber);
}
