package com.stephen.Match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface Match_Repository_Slot extends JpaRepository<Match_Slot, Long> {
    List<Match_Slot> findByMatchID(Long matchID);
    Optional<Match_Slot> findByMatchIDAndSlotNumber(Long matchID, int slotNumber);
    List<Match_Slot> findByMatchIDAndStatus(Long matchID, Match_Slot.Status status);
    Optional<Match_Slot> findByFrameID(Long frameID);  // ← add this
}