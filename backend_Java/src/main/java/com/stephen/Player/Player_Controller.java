package com.stephen.Player;

import com.stephen.Player.DTO.Player_DTO;
import com.stephen.Player.DTO.Player_Request;
import com.stephen.Player.DTO.Player_Response;
import com.stephen.PostgreSQL.Api_Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class Player_Controller {

    private final Player_Service playerService;

    public Player_Controller(Player_Service playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> publicPing() {
        return ResponseEntity.ok("Backend is alive!");
    }

    @GetMapping("/players")
    public ResponseEntity<Api_Response<Player_Response>> getPlayers() {
        List<Player_DTO> players = playerService.getAllPlayersDTO();
        return ResponseEntity.ok(
                new Api_Response<>(
                        new Player_Response(players)
                )
        );
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player_Entity> getPlayer(@PathVariable Long id) {
        return playerService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/players/{id}")
    public ResponseEntity<Player_Entity> updatePlayer(
            @PathVariable Long id,
            @RequestBody Player_Request req) {
        return ResponseEntity.ok(playerService.updatePlayer(id, req));
    }

    @PatchMapping("/players/{id}/captain")
    public ResponseEntity<Player_Entity> setCaptain(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {
        Player_Entity updated = Boolean.TRUE.equals(body.get("isCaptain"))
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