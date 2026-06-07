package com.stephen.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface Player_Repository extends JpaRepository<Player_Entity, Long> {
    Optional<Player_Entity> findByFirebaseUid(String firebaseUid);
    List<Player_Entity> findByLastName(String lastName);
    List<Player_Entity> findByIsCaptainTrue();
    boolean existsByFirebaseUid(String firebaseUid);
}