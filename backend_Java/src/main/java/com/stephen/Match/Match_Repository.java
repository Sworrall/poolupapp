package com.stephen.Match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface Match_Repository extends JpaRepository<Match, Long> {
    List<Match> findByIsPlayed(boolean isPlayed);
    List<Match> findByIsBye(boolean isBye);
    Optional<Match_Slot> findByFrameID(Long frameID);
    Optional<Match_Slot> findByFrameIDAndIsPlayed(Long frameID, boolean isPlayed);
    Optional<Match_Slot> findByFrameIDAndIsBye(Long frameID, boolean isBye);
    Optional<Match> findByID(Long ID);
}