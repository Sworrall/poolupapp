package com.stephen.Player;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface Player_Repository extends JpaRepository<Player, Long> {
    Optional<Player> findByFirebaseUid(String firebaseUid);
    List<Player> findByLastName(String lastName);
    List<Player> findByIsCaptainTrue();
    boolean existsByFirebaseUid(String firebaseUid);
    Optional<Player> findById(Long Id);
    void deleteById(@NonNull Long Id);
}