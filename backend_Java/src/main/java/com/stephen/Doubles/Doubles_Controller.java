package com.stephen.Doubles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/Doubles")
public class Doubles_Controller {

    private static final Logger log = LoggerFactory.getLogger(Doubles_Controller.class);
    private final Doubles_Service doublesService;

    public Doubles_Controller(Doubles_Service doublesService) {
        this.doublesService = doublesService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> publicPing() {
        return ResponseEntity.ok("Doubles_Controller is alive!");
    }

    @PostMapping
    public ResponseEntity<Doubles_Entity> createDoubles(@RequestBody Doubles_Request req) {
        log.info("POST /api/Doubles — players: {}, {}", req.getPlayer1Id(), req.getPlayer2Id());
        return ResponseEntity.ok(doublesService.createDoubles(req));
    }

    @GetMapping("/DoublesTeam")
    public ResponseEntity<Doubles_ListResponse> getAllDoubles() {

        List<Doubles_Response> results =
                doublesService.getAllDoubles()
                        .stream()
                        .map(Doubles_Response::fromEntity)
                        .toList();

        return ResponseEntity.ok(
                new Doubles_ListResponse(results)
        );
    }

    @GetMapping("/DoublesTeam/{doublesId}")
    public ResponseEntity<Doubles_Entity> getDoubles(@PathVariable Long doublesId) {
        return doublesService.getById(doublesId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/DoublesTeam/{doublesId}")
    public ResponseEntity<Doubles_Entity> updateDoubles(
            @PathVariable Long doublesId,
            @RequestBody Doubles_Request req) {
        log.info("PUT /api/doubles/{}", doublesId);
        return ResponseEntity.ok(doublesService.updateDoubles(doublesId, req));
    }

    @PatchMapping("/DoublesTeam/{doublesId}/captain/{playerId}")
    public ResponseEntity<Doubles_Entity> setCaptain(
            @PathVariable Long doublesId,
            @PathVariable Long playerId) {
        log.info("PATCH /api/doubles/{}/captain/{}", doublesId, playerId);
        return ResponseEntity.ok(doublesService.setCaptain(doublesId, playerId));
    }

    @DeleteMapping("/DoublesTeam/{doublesId}")
    public ResponseEntity<Void> deleteDoubles(@PathVariable Long doublesId) {
        log.info("DELETE /api/doubles/{}", doublesId);
        doublesService.deleteDoubles(doublesId);
        return ResponseEntity.noContent().build();
    }

    // --- EXCEPTION HANDLING ---
    @ExceptionHandler(DoublesNotFoundException.class)
    public ResponseEntity<String> handleNotFound(DoublesNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}
