package com.stephen.Tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Tournament_Repository extends JpaRepository<Tournament_Entity, Long> {
    List<Tournament_Entity> findByPartyType(PartyType partyType);
    List<Tournament_Entity> findByIsCompleteFalse();
    List<Tournament_Entity> findByPartyTypeAndIsCompleteTrue(PartyType partyType);
}
