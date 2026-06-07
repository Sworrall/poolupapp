package com.stephen.Doubles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface Doubles_Repository extends JpaRepository<Doubles_Entity, Long> {
    Optional<Doubles_Entity> findByFirebaseUid(String firebaseUid);
    boolean existsByFirebaseUid(String firebaseUid);
}