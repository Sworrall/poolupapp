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
    public ResponseEntity<Team_Entity> createTeam(@RequestBody Team_Request req) {
        return ResponseEntity.ok(teamService.createTeam(req));
    }

    @GetMapping
    public ResponseEntity<List<Team_Entity>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team_Entity> getTeam(@PathVariable Long Id) {
        return teamService.getById(Id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team_Entity> updateTeam(
            @PathVariable Long Id,
            @RequestBody Team_Request req) {
        return ResponseEntity.ok(teamService.updateTeam(Id, req));
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Team_Entity> addPlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.addPlayer(teamId, playerId));
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Team_Entity> removePlayer(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.removePlayer(teamId, playerId));
    }

    @PatchMapping("/{teamId}/captain/{playerId}")
    public ResponseEntity<Team_Entity> setCaptain(
            @PathVariable Long teamId,
            @PathVariable Long playerId) {
        return ResponseEntity.ok(teamService.setCaptain(teamId, playerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long Id) {
        teamService.deleteTeam(Id);
        return ResponseEntity.noContent().build();
    }
}