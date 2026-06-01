package com.stephen.Tournament;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface Tournament_Repository extends JpaRepository<Tournament, Long> {

    /**
     * All tournaments for a given party type (e.g. all SINGLES tournaments).
     */
    List<Tournament> findByPartyType(PartyType partyType);

    /**
     * All incomplete tournaments — useful for resuming in-progress events.
     */
    List<Tournament> findByIsCompleteFalse();

    /**
     * All complete tournaments of a given party type.
     */
    List<Tournament> findByPartyTypeAndIsCompleteTrue(PartyType partyType);

    Optional<Tournament> findByID(Long ID);
}
