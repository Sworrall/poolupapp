package com.stephen.Match;

import com.stephen.Frame.Frame_Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface Match_Repository extends JpaRepository<Match_Entity, Long> {
    List<Match_Entity> findByIsPlayed(boolean isPlayed);
    List<Match_Entity> findByIsBye(boolean isBye);
    Optional<Match_Slot> findByFrames_Id(Long id);
    Optional<Match_Slot> findByFrames_IdAndIsPlayed(Long frameId, boolean isPlayed);
    Optional<Match_Slot> findByFrames_IdAndIsBye(Long frameId, boolean isBye);
}