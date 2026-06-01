package com.stephen.Doubles;

import com.stephen.Player.Player;
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

    public Doubles createDoubles(Doubles_Request req) {
        if (req.getFirebaseUID() != null && doublesRepo.existsByFirebaseUID(req.getFirebaseUID())) {
            throw new IllegalArgumentException(
                    "A doubles pair already exists for Firebase UID: " + req.getFirebaseUID());
        }

        Player p1 = playerRepo.findByID(req.getPlayer1ID())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayer1ID()));
        Player p2 = playerRepo.findByID(req.getPlayer2ID())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayer2ID()));

        Doubles doubles = new Doubles();
        doubles.setPlayers(p1, p2);

        if (req.getTeamName() != null) doubles.setTeamName(req.getTeamName());
        if (req.getFirebaseUID() != null) doubles.setFirebaseUID(req.getFirebaseUID());
        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            doubles.setContactDetails(new Doubles_ContactDetails(
                    req.getPhoneNumber(), req.getAddress()));
        }

        return doublesRepo.save(doubles);
    }

    public Optional<Doubles> getByID(Long ID) {
        return doublesRepo.findByID(ID);
    }

    public Optional<Doubles> getByFirebaseUID(String uID) {
        return doublesRepo.findByFirebaseUID(uID);
    }

    public List<Doubles> getAllDoubles() {
        return doublesRepo.findAll();
    }

    public Doubles updateDoubles(Long ID, Doubles_Request req) {
        Doubles doubles = doublesRepo.findByID(ID)
                .orElseThrow(() -> new DoublesNotFoundException(ID));

        if (req.getPlayer1ID() != null && req.getPlayer2ID() != null) {
            Player p1 = playerRepo.findByID(req.getPlayer1ID())
                    .orElseThrow(() -> new PlayerNotFoundException(req.getPlayer1ID()));
            Player p2 = playerRepo.findByID(req.getPlayer2ID())
                    .orElseThrow(() -> new PlayerNotFoundException(req.getPlayer2ID()));
            doubles.setPlayers(p1, p2);
        }

        if (req.getTeamName() != null) doubles.setTeamName(req.getTeamName());
        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            doubles.setContactDetails(new Doubles_ContactDetails(
                    req.getPhoneNumber(), req.getAddress()));
        }

        return doublesRepo.save(doubles);
    }

    public Doubles setCaptain(Long doublesID, Long playerID) {
        Doubles doubles = doublesRepo.findByID(doublesID)
                .orElseThrow(() -> new DoublesNotFoundException(doublesID));
        Player player = playerRepo.findByID(playerID)
                .orElseThrow(() -> new PlayerNotFoundException(playerID));
        doubles.setCaptain(player);
        return doublesRepo.save(doubles);
    }

    public void deleteDoubles(Long ID) {
        doublesRepo.deleteByID(ID);
    }
}
