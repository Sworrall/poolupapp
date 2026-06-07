package com.stephen.Frame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface Frame_Repository extends JpaRepository<Frame_Entity, Long> {
    List<Frame_Entity> findByIsPlayed(boolean isPlayed);
    List<Frame_Entity> findByIsBye(boolean isBye);
}