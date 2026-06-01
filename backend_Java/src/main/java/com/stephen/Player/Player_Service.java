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
        if (playerRepo.existsByFirebaseUID(req.getFirebaseUID())) {
            throw new IllegalArgumentException(
                    "A player already exists for Firebase UID: " + req.getFirebaseUID()
            );
        }
        Player player = new Player();
        player.setFirstName(req.getFirstName());
        player.setLastName(req.getLastName());
        player.setNickName(req.getNickName());
        player.setPhoneNumber(req.getPhoneNumber());
        player.setFirebaseUID(req.getFirebaseUID());
        return playerRepo.save(player);
    }

    public Optional<Player> getByID(Long ID) {
        return playerRepo.findByID(ID);
    }

    public Optional<Player> getByFirebaseUID(String uID) {
        return playerRepo.findByFirebaseUID(uID);
    }

    public List<Player> getAllPlayers() {
        return playerRepo.findAll();
    }

    public Player updatePlayer(Long ID, Player_Request req) {
        Player player = playerRepo.findByID(ID)
                .orElseThrow(() -> new PlayerNotFoundException(ID));
        player.setFirstName(req.getFirstName());
        player.setLastName(req.getLastName());
        player.setNickName(req.getNickName());
        player.setPhoneNumber(req.getPhoneNumber());
        return playerRepo.save(player);
    }

    public Player makeCaptain(Long ID) {
        Player player = playerRepo.findByID(ID)
                .orElseThrow(() -> new PlayerNotFoundException(ID));
        player.setCaptain(true);
        return playerRepo.save(player);
    }

    public Player removeCaptain(Long ID) {
        Player player = playerRepo.findByID(ID)
                .orElseThrow(() -> new PlayerNotFoundException(ID));
        player.setCaptain(false);
        return playerRepo.save(player);
    }

    public void deletePlayer(Long ID) {
        playerRepo.deleteByID(ID);
    }
}