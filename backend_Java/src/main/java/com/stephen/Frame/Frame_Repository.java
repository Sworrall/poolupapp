package com.stephen.Frame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface Frame_Repository extends JpaRepository<Frame, Long> {
    List<Frame> findByIsPlayed(boolean isPlayed);
    List<Frame> findByIsBye(boolean isBye);
    Optional<Frame> findById(Long Id);
}