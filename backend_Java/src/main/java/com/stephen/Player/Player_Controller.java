package com.stephen.Player;

import com.stephen.Match.Match_Request_Team;
import com.stephen.Match.Match_Team;
import com.stephen.PostgreSQL.Api_Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Player")
public class Player_Controller {

    private final Player_Service playerService;

    public Player_Controller(Player_Service playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> publicPing() {
        return ResponseEntity.ok("Player_controller is alive!");
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

    @PostMapping("/players")
    public ResponseEntity<Player_DTO> createPlayer(@RequestBody Player_Request req) {
        return ResponseEntity.ok(new Player_DTO(playerService.createPlayer(req)));
    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<Player_Entity> getPlayer(@PathVariable Long playerId) {
        return playerService.getById(playerId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/players/{playerId}")
    public ResponseEntity<Player_Entity> updatePlayer(
            @PathVariable Long playerId,
            @RequestBody Player_Request req) {
        return ResponseEntity.ok(playerService.updatePlayer(playerId, req));
    }

    @PatchMapping("/players/{playerId}/captain")
    public ResponseEntity<Player_Entity> setCaptain(
            @PathVariable Long playerId,
            @RequestBody Map<String, Boolean> body) {
        Player_Entity updated = Boolean.TRUE.equals(body.get("isCaptain"))
                ? playerService.makeCaptain(playerId)
                : playerService.removeCaptain(playerId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/players/{playerId}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long playerId) {
        playerService.deletePlayer(playerId);
        return ResponseEntity.noContent().build();
    }
}