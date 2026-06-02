package com.stephen.Match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface Match_Repository_Slot extends JpaRepository<Match_Slot, Long> {
    List<Match_Slot> findByMatchId(Long matchId);
    Optional<Match_Slot> findByMatchIdAndSlotNumber(Long matchId, int slotNumber);
    List<Match_Slot> findByMatchIdAndStatus(Long matchId, Match_Slot.Status status);
    Optional<Match_Slot> findByFrameId(Long frameId);  // ← add this
}