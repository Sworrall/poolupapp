package com.stephen.Player;

import com.stephen.Player.dto.Player_Request;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class Player_Service {

    private final Player_Repository playerRepo;

    public Player_Service(Player_Repository playerRepo) {
        this.playerRepo = playerRepo;
    }

    public Player_Entity createPlayer(Player_Request req) {
        if (playerRepo.existsByFirebaseUid(req.getFirebaseUid())) {
            throw new IllegalArgumentException(
                    "A player already exists for Firebase Uid: " + req.getFirebaseUid()
            );
        }
        Player_Entity player = new Player_Entity();
        player.setFirstName(req.getFirstName());
        player.setLastName(req.getLastName());
        player.setNickName(req.getNickName());
        player.setPhoneNumber(req.getPhoneNumber());
        player.setFirebaseUid(req.getFirebaseUid());
        try {
            return playerRepo.save(player);
        } catch (org.springframework.dao.DataIntegrityViolationException e){
            throw new IllegalArgumentException("A player already exists for Firebase Uid: " + req.getFirebaseUid());
        }
    }

    public Optional<Player_Entity> getById(Long Id) {
        return playerRepo.findById(Id);
    }

    public Optional<Player_Entity> getByFirebaseUid(String uid) {
        return playerRepo.findByFirebaseUid(uid);
    }

    public List<Player_Entity> getAllPlayers() {
        return playerRepo.findAll();
    }

    public Player_Entity updatePlayer(Long Id, Player_Request req) {
        Player_Entity player = playerRepo.findById(Id)
                .orElseThrow(() -> new PlayerNotFoundException(Id));
        player.setFirstName(req.getFirstName());
        player.setLastName(req.getLastName());
        player.setNickName(req.getNickName());
        player.setPhoneNumber(req.getPhoneNumber());
        return playerRepo.save(player);
    }

    public Player_Entity makeCaptain(Long Id) {
        Player_Entity player = playerRepo.findById(Id).orElseThrow(() -> new PlayerNotFoundException(Id));
        player.setCaptain(true);
        return playerRepo.save(player);
    }

    public Player_Entity removeCaptain(Long Id) {
        Player_Entity player = playerRepo.findById(Id).orElseThrow(() -> new PlayerNotFoundException(Id));
        player.setCaptain(false);
        return playerRepo.save(player);
    }

    public void deletePlayer(Long Id) {
        playerRepo.deleteById(Id);
    }
}