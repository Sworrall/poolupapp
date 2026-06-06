package com.stephen.Tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface Tournament_Repository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByPartyType(PartyType partyType);
    List<Tournament> findByIsCompleteFalse();
    List<Tournament> findByPartyTypeAndIsCompleteTrue(PartyType partyType);
}
