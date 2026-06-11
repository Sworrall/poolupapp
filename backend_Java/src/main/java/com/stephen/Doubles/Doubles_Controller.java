package com.stephen.Doubles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doubles")
public class Doubles_Controller {

    private static final Logger log = LoggerFactory.getLogger(Doubles_Controller.class);
    private final Doubles_Service doublesService;

    public Doubles_Controller(Doubles_Service doublesService) {
        this.doublesService = doublesService;
    }

    @PostMapping
    public ResponseEntity<Doubles_Entity> createDoubles(@RequestBody Doubles_Request req) {
        log.info("POST /api/doubles — players: {}, {}", req.getPlayer1Id(), req.getPlayer2Id());
        return ResponseEntity.ok(doublesService.createDoubles(req));
    }

    @GetMapping
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

    @GetMapping("/{id}")
    public ResponseEntity<Doubles_Entity> getDoubles(@PathVariable Long id) {
        return doublesService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doubles_Entity> updateDoubles(
            @PathVariable Long id,
            @RequestBody Doubles_Request req) {
        log.info("PUT /api/doubles/{}", id);
        return ResponseEntity.ok(doublesService.updateDoubles(id, req));
    }

    @PatchMapping("/{doublesId}/captain/{playerId}")
    public ResponseEntity<Doubles_Entity> setCaptain(
            @PathVariable Long doublesId,
            @PathVariable Long playerId) {
        log.info("PATCH /api/doubles/{}/captain/{}", doublesId, playerId);
        return ResponseEntity.ok(doublesService.setCaptain(doublesId, playerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoubles(@PathVariable Long id) {
        log.info("DELETE /api/doubles/{}", id);
        doublesService.deleteDoubles(id);
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
