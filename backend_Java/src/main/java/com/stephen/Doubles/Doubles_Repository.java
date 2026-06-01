package com.stephen.Doubles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface Doubles_Repository extends JpaRepository<Doubles, Long> {
    Optional<Doubles> findByFirebaseUID(String firebaseUID);
    boolean existsByFirebaseUID(String firebaseUID);
    Optional<Doubles> findByID(Long ID);
    void deleteByID(Long ID);
}