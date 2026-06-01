package com.stephen.Match;

import com.stephen.Match.Doubles.Match_Doubles;
import com.stephen.Match.Doubles.Match_Request_Doubles;
import com.stephen.Match.Singles.Match_Singles;
import com.stephen.Match.Singles.Match_Request_Singles;
import com.stephen.Match.Team.Match_Team;
import com.stephen.Match.Team.Match_Request_Team;
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
    public ResponseEntity<Match> getMatch(@PathVariable Long ID) {
        return matchService.getByID(ID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<List<Match_Slot>> getSlots(@PathVariable Long ID) {
        return ResponseEntity.ok(matchService.getSlotsForMatch(ID));
    }

    @PatchMapping("/{matchId}/slots/{slotNumber}/player-a")
    public ResponseEntity<Match_Slot> assignPlayerA(
            @PathVariable Long matchID,
            @PathVariable int slotNumber,
            @RequestBody Match_PlayerRequest_Slot req) {
        return ResponseEntity.ok(matchService.assignPlayerA(matchID, slotNumber, req));
    }

    @PatchMapping("/{matchId}/slots/{slotNumber}/player-b")
    public ResponseEntity<Match_Slot> assignPlayerB(
            @PathVariable Long matchID,
            @PathVariable int slotNumber,
            @RequestBody Match_PlayerRequest_Slot req) {
        return ResponseEntity.ok(matchService.assignPlayerB(matchID, slotNumber, req));
    }

    @GetMapping("/unplayed")
    public ResponseEntity<List<Match>> getUnplayed() {
        return ResponseEntity.ok(matchService.getUnplayed());
    }
}