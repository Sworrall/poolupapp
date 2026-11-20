package com.stephen.Team;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Team")
public class Team_Controller {

    private final Team_Service teamService;

    public Team_Controller(Team_Service teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> publicPing() {
        return ResponseEntity.ok("Team_Controller is alive!");
    }

    @PostMapping
    public ResponseEntity<Team_DetailResponse> createTeam(@RequestBody Team_Request req) {
        return ResponseEntity.ok(teamService.createTeam(req));
    }

    @GetMapping("/Teams")
    public ResponseEntity<List<Team_ListResponse>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/Teams/{teamId}")
    public ResponseEntity<Team_DetailResponse> getTeam(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getById(teamId));
    }

    @PutMapping("/Teams/{Teams}")
    public ResponseEntity<Team_DetailResponse> updateTeam(
            @PathVariable Long teamId,
            @RequestBody Team_Request req) {
        return ResponseEntity.ok(teamService.updateTeam(teamId, req));
    }

    @PostMapping("/Teams/{teamId}/players/{playerId}")
    public ResponseEntity<Team_DetailResponse> addPlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.addPlayer(teamId, playerId));
    }

    @DeleteMapping("/Teams/{teamId}/players/{playerId}")
    public ResponseEntity<Team_DetailResponse> removePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.removePlayer(teamId, playerId));
    }

    @PatchMapping("/Teams/{teamId}/captain/{playerId}")
    public ResponseEntity<Team_DetailResponse> setCaptain(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.setCaptain(teamId, playerId));
    }

    @DeleteMapping("/Teams/{teamId}")   // missing the opening {
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.noContent().build();
    }
}