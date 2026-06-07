package com.stephen.Doubles;

import com.stephen.Player.Player_Entity;
import com.stephen.Player.Player_Repository;
import com.stephen.Player.PlayerNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class Doubles_Service {

    private final Doubles_Repository doublesRepo;
    private final Player_Repository playerRepo;

    public Doubles_Service(Doubles_Repository doublesRepo, Player_Repository playerRepo) {
        this.doublesRepo = doublesRepo;
        this.playerRepo = playerRepo;
    }

    public Doubles_Entity createDoubles(Doubles_Request req) {
        if (req.getFirebaseUid() != null && doublesRepo.existsByFirebaseUid(req.getFirebaseUid())) {
            throw new IllegalArgumentException(
                    "A doubles pair already exists for Firebase Uid: " + req.getFirebaseUid());
        }

        Player_Entity p1 = playerRepo.findById(req.getPlayer1Id())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayer1Id()));
        Player_Entity p2 = playerRepo.findById(req.getPlayer2Id())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayer2Id()));

        Doubles_Entity doubles = new Doubles_Entity();
        doubles.setPlayers(p1, p2);

        if (req.getTeamName() != null) doubles.setTeamName(req.getTeamName());
        if (req.getFirebaseUid() != null) doubles.setFirebaseUid(req.getFirebaseUid());
        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            doubles.setContactDetails(new Doubles_ContactDetails(
                    req.getPhoneNumber(), req.getAddress()));
        }

        return doublesRepo.save(doubles);
    }

    public Optional<Doubles_Entity> getById(Long Id) {
        return doublesRepo.findById(Id);
    }

    public Optional<Doubles_Entity> getByFirebaseUid(String uId) {
        return doublesRepo.findByFirebaseUid(uId);
    }

    public List<Doubles_Entity> getAllDoubles() {
        return doublesRepo.findAll();
    }

    public Doubles_Entity updateDoubles(Long Id, Doubles_Request req) {
        Doubles_Entity doubles = doublesRepo.findById(Id)
                .orElseThrow(() -> new DoublesNotFoundException(Id));

        if (req.getPlayer1Id() != null && req.getPlayer2Id() != null) {
            Player_Entity p1 = playerRepo.findById(req.getPlayer1Id())
                    .orElseThrow(() -> new PlayerNotFoundException(req.getPlayer1Id()));
            Player_Entity p2 = playerRepo.findById(req.getPlayer2Id())
                    .orElseThrow(() -> new PlayerNotFoundException(req.getPlayer2Id()));
            doubles.setPlayers(p1, p2);
        }

        if (req.getTeamName() != null) doubles.setTeamName(req.getTeamName());
        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            doubles.setContactDetails(new Doubles_ContactDetails(
                    req.getPhoneNumber(), req.getAddress()));
        }

        return doublesRepo.save(doubles);
    }

    public Doubles_Entity setCaptain(Long doublesId, Long playerId) {
        Doubles_Entity doubles = doublesRepo.findById(doublesId)
                .orElseThrow(() -> new DoublesNotFoundException(doublesId));
        Player_Entity player = playerRepo.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        doubles.setCaptain(player);
        return doublesRepo.save(doubles);
    }

    public void deleteDoubles(Long Id) {
        doublesRepo.deleteById(Id);
    }
}
