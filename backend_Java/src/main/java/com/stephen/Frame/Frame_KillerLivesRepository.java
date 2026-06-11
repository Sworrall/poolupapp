package com.stephen.Frame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Frame_KillerLivesRepository extends JpaRepository<Frame_KillerLives, Long> {

    /**
     * All player-lives records for a given killer frame.
     * Used to retrieve current lives state during result recording.
     */
    List<Frame_KillerLives> findByFrameId(Long id);

    /**
     * Lives record for a specific player within a specific frame.
     * Used to update a single player's lives after a frame result.
     */
    java.util.Optional<Frame_KillerLives> findByFrameIdAndPlayerId(Long frameId, Long playerId);
}
