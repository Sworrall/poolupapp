package com.stephen.Team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface Team_Repository extends JpaRepository<Team, Long> {
    Optional<Team> findByFirebaseUid(String firebaseUid);
    boolean existsByFirebaseUid(String firebaseUid);
    Optional<Team> findByTeamName(String name);
}