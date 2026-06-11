package com.stephen.Match;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class Match_Controller {

    private final Match_Service matchService;

    public Match_Controller(Match_Service matchService) {
        this.matchService = matchService;
    }

    @PostMapping("/singles")
    public ResponseEntity<Match_Singles> createSingles(@RequestBody Match_Request_Singles req) {
        return ResponseEntity.ok(matchService.createSinglesMatch(req));
    }

    @PostMapping("/doubles")
    public ResponseEntity<Match_Doubles> createDoubles(@RequestBody Match_Request_Doubles req) {
        return ResponseEntity.ok(matchService.createDoublesMatch(req));
    }

    @PostMapping("/team")
    public ResponseEntity<Match_Team> createTeam(@RequestBody Match_Request_Team req) {
        return ResponseEntity.ok(matchService.createTeamMatch(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match_Entity> getMatch(@PathVariable Long id) {
        return matchService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<Match_Slot>> getSlots(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getSlotsForMatch(id));
    }

    @PatchMapping("/{matchId}/slots/{slotNumber}/player-a")
    public ResponseEntity<Match_Slot> assignPlayerA(
            @PathVariable Long id,
            @PathVariable int slotNumber,
            @RequestBody Match_PlayerRequest_Slot req) {
        return ResponseEntity.ok(matchService.assignPlayerA(id, slotNumber, req));
    }

    @PatchMapping("/{matchId}/slots/{slotNumber}/player-b")
    public ResponseEntity<Match_Slot> assignPlayerB(
            @PathVariable Long id,
            @PathVariable int slotNumber,
            @RequestBody Match_PlayerRequest_Slot req) {
        return ResponseEntity.ok(matchService.assignPlayerB(id, slotNumber, req));
    }

    @GetMapping("/unplayed")
    public ResponseEntity<List<Match_Entity>> getUnplayed() {
        return ResponseEntity.ok(matchService.getUnplayed());
    }
}