package com.stephen.Team;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class Team_Controller {

    private final Team_Service teamService;

    public Team_Controller(Team_Service teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public ResponseEntity<Team_DetailResponse> createTeam(@RequestBody Team_Request req) {
        return ResponseEntity.ok(teamService.createTeam(req));
    }

    @GetMapping
    public ResponseEntity<List<Team_ListResponse>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team_DetailResponse> getTeam(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team_DetailResponse> updateTeam(
            @PathVariable Long id,
            @RequestBody Team_Request req) {
        return ResponseEntity.ok(teamService.updateTeam(id, req));
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Team_DetailResponse> addPlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.addPlayer(teamId, playerId));
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Team_DetailResponse> removePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.removePlayer(teamId, playerId));
    }

    @PatchMapping("/{teamId}/captain/{playerId}")
    public ResponseEntity<Team_DetailResponse> setCaptain(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.setCaptain(teamId, playerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}