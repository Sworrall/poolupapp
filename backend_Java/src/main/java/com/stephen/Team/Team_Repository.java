package com.stephen.Team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface Team_Repository extends JpaRepository<Team, Long> {
    Optional<Team> findByFirebaseUID(String firebaseUID);
    boolean existsByFirebaseUID(String firebaseUID);
    Optional<Team> findByID(Long id);
    Optional<Team> findByName(String name);
    void deleteByID(Long ID);
}