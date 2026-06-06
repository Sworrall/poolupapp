package com.stephen.Player;

import com.stephen.Player.dto.Player_Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Player_Controller {

    private static final Logger log = LoggerFactory.getLogger(Player_Controller.class);
    private final Player_Service playerService;

    public Player_Controller(Player_Service playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> publicPing() {
        return ResponseEntity.ok("Backend is alive!");
    }

    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player_Request req) {
        return ResponseEntity.ok(playerService.createPlayer(req));
    }

    @GetMapping("/players")
    public ResponseEntity<List<Player>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayer(@PathVariable Long id) {
        return playerService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(
            @PathVariable Long id,
            @RequestBody Player_Request req) {
        return ResponseEntity.ok(playerService.updatePlayer(id, req));
    }

    @PatchMapping("/players/{id}/captain")
    public ResponseEntity<Player> setCaptain(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        Player updated = Boolean.TRUE.equals(body.get("isCaptain"))
                ? playerService.makeCaptain(id)
                : playerService.removeCaptain(id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}