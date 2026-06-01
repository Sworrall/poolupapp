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
    public ResponseEntity<Doubles> createDoubles(@RequestBody Doubles_Request req) {
        log.info("POST /api/doubles — players: {}, {}", req.getPlayer1ID(), req.getPlayer2ID());
        return ResponseEntity.ok(doublesService.createDoubles(req));
    }

    @GetMapping
    public ResponseEntity<List<Doubles>> getAllDoubles() {
        return ResponseEntity.ok(doublesService.getAllDoubles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doubles> getDoubles(@PathVariable Long ID) {
        return doublesService.getByID(ID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doubles> updateDoubles(
            @PathVariable Long ID,
            @RequestBody Doubles_Request req) {
        log.info("PUT /api/doubles/{}", ID);
        return ResponseEntity.ok(doublesService.updateDoubles(ID, req));
    }

    @PatchMapping("/{doublesId}/captain/{playerId}")
    public ResponseEntity<Doubles> setCaptain(
            @PathVariable Long doublesID,
            @PathVariable Long playerID) {
        log.info("PATCH /api/doubles/{}/captain/{}", doublesID, playerID);
        return ResponseEntity.ok(doublesService.setCaptain(doublesID, playerID));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoubles(@PathVariable Long ID) {
        log.info("DELETE /api/doubles/{}", ID);
        doublesService.deleteDoubles(ID);
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
