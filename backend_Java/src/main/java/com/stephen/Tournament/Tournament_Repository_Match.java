package com.stephen.Tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Tournament_Repository_Match extends JpaRepository<Tournament_Match, Long> {

    /**
     * All fixtures for a tournament, ordered for deterministic reconstruction
     * of the original 2D matchList structure:
     *   - outer list index = roundNumber  (KO round / GroupStage group / 0 for flat formats)
     *   - inner list index = sequence
     */
    List<Tournament_Match> findByTournamentIDOrderByRoundNumberAscSequenceAsc(Long tournamentID);

    /**
     * All fixtures for a specific round/group within a tournament.
     * Used by KO service to retrieve only the current round's matches.
     */
    List<Tournament_Match> findByTournamentIDAndRoundNumberOrderBySequenceAsc(Long tournamentID, int roundNumber);

    /**
     * Highest round number recorded for a tournament.
     * Used by KO service to determine the current round index without loading all fixtures.
     */
    @org.springframework.data.jpa.repository.Query(
            "SELECT MAX(tm.roundNumber) FROM TournamentMatch tm WHERE tm.tournament.ID = :tournamentID"
    )
    Integer findMaxRoundNumber(@org.springframework.data.repository.query.Param("tournamentID") Long tournamentID);

    /**
     * Count of fixtures in a given round. Used to validate round completeness.
     */
    long countByTournamentIDAndRoundNumber(Long tournamentID, int roundNumber);
}
