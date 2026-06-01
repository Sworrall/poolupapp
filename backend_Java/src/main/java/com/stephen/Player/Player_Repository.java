package com.stephen.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface Player_Repository extends JpaRepository<Player, Long> {
    Optional<Player> findByFirebaseUID(String firebaseUID);
    List<Player> findByLastName(String lastName);
    List<Player> findByIsCaptainTrue();
    boolean existsByFirebaseUID(String firebaseUID);
    Optional<Player> findByID(Long ID);
    void deleteByID(Long ID);
}