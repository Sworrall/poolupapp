package com.stephen.Match;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/match")
public class Match_Controller {

    private final Match_Service matchService;

    public Match_Controller(Match_Service matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> publicPing() {
        return ResponseEntity.ok("Match_Controller is alive!");
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

    @GetMapping("/{matchId}")
    public ResponseEntity<Match_Entity> getMatch(@PathVariable Long matchId) {
        return matchService.getById(matchId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{matchId}/slots")
    public ResponseEntity<List<Match_Slot>> getSlots(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchService.getSlotsForMatch(matchId));
    }

    @PatchMapping("/{matchId}/slots/{slotNumber}/player-a")
    public ResponseEntity<Match_Slot> assignPlayerA(
            @PathVariable Long matchId,
            @PathVariable int slotNumber,
            @RequestBody Match_PlayerRequest_Slot req) {
        return ResponseEntity.ok(matchService.assignPlayerA(matchId, slotNumber, req));
    }

    @PatchMapping("/{matchId}/slots/{slotNumber}/player-b")
    public ResponseEntity<Match_Slot> assignPlayerB(
            @PathVariable Long matchId,
            @PathVariable int slotNumber,
            @RequestBody Match_PlayerRequest_Slot req) {
        return ResponseEntity.ok(matchService.assignPlayerB(matchId, slotNumber, req));
    }

    @GetMapping("/unplayed")
    public ResponseEntity<List<Match_Entity>> getUnplayed() {
        return ResponseEntity.ok(matchService.getUnplayed());
    }
}