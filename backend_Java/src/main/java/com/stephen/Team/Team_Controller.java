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
    public ResponseEntity<Team> createTeam(@RequestBody Team_Request req) {
        return ResponseEntity.ok(teamService.createTeam(req));
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Team> getTeam(@PathVariable Long ID) {
        return teamService.getByID(ID)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(
            @PathVariable Long ID,
            @RequestBody Team_Request req) {
        return ResponseEntity.ok(teamService.updateTeam(ID, req));
    }

    @PostMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Team> addPlayer(
            @PathVariable Long teamID,
            @PathVariable Long playerID) {
        return ResponseEntity.ok(teamService.addPlayer(teamID, playerID));
    }

    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Team> removePlayer(
            @PathVariable Long teamID,
            @PathVariable Long playerID) {
        return ResponseEntity.ok(teamService.removePlayer(teamID, playerID));
    }

    @PatchMapping("/{teamId}/captain/{playerId}")
    public ResponseEntity<Team> setCaptain(
            @PathVariable Long teamID,
            @PathVariable Long playerID) {
        return ResponseEntity.ok(teamService.setCaptain(teamID, playerID));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long ID) {
        teamService.deleteTeam(ID);
        return ResponseEntity.noContent().build();
    }
}