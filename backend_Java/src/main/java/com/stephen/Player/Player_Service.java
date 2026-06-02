package com.stephen.Player;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class Player_Service {

    private final Player_Repository playerRepo;

    public Player_Service(Player_Repository playerRepo) {
        this.playerRepo = playerRepo;
    }

    public Player createPlayer(Player_Request req) {
        if (playerRepo.existsByFirebaseUid(req.getFirebaseUid())) {
            throw new IllegalArgumentException(
                    "A player already exists for Firebase Uid: " + req.getFirebaseUid()
            );
        }
        Player player = new Player();
        player.setFirstName(req.getFirstName());
        player.setLastName(req.getLastName());
        player.setNickName(req.getNickName());
        player.setPhoneNumber(req.getPhoneNumber());
        player.setFirebaseUid(req.getFirebaseUid());
        return playerRepo.save(player);
    }

    public Optional<Player> getById(Long Id) {
        return playerRepo.findById(Id);
    }

    public Optional<Player> getByFirebaseUid(String uid) {
        return playerRepo.findByFirebaseUid(uid);
    }

    public List<Player> getAllPlayers() {
        return playerRepo.findAll();
    }

    public Player updatePlayer(Long Id, Player_Request req) {
        Player player = playerRepo.findById(Id)
                .orElseThrow(() -> new PlayerNotFoundException(Id));
        player.setFirstName(req.getFirstName());
        player.setLastName(req.getLastName());
        player.setNickName(req.getNickName());
        player.setPhoneNumber(req.getPhoneNumber());
        return playerRepo.save(player);
    }

    public Player makeCaptain(Long Id) {
        Player player = playerRepo.findById(Id)
                .orElseThrow(() -> new PlayerNotFoundException(Id));
        player.setCaptain(true);
        return playerRepo.save(player);
    }

    public Player removeCaptain(Long Id) {
        Player player = playerRepo.findById(Id)
                .orElseThrow(() -> new PlayerNotFoundException(Id));
        player.setCaptain(false);
        return playerRepo.save(player);
    }

    public void deletePlayer(Long Id) {
        playerRepo.deleteById(Id);
    }
}