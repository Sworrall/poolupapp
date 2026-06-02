package com.stephen.Team;

import com.stephen.Player.Player;
import com.stephen.Player.Player_Repository;
import com.stephen.Player.PlayerNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class Team_Service {

    private final Team_Repository teamRepo;
    private final Player_Repository playerRepo;

    public Team_Service(Team_Repository teamRepo, Player_Repository playerRepo) {
        this.teamRepo = teamRepo;
        this.playerRepo = playerRepo;
    }

    public Team createTeam(Team_Request req) {
        Team team = new Team();
        team.setTeamName(req.getTeamName());
        team.setFirebaseUid(req.getFirebaseUid());
        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            team.setContactDetails(new Team_ContactDetails(
                    req.getPhoneNumber(), req.getAddress()
            ));
        }
        return teamRepo.save(team);
    }

    public Optional<Team> getById(Long Id) {
        return teamRepo.findById(Id);
    }

    public List<Team> getAllTeams() {
        return teamRepo.findAll();
    }

    public Team updateTeam(Long Id, Team_Request req) {
        Team team = teamRepo.findById(Id)
                .orElseThrow(() -> new TeamNotFoundException(Id));
        team.setTeamName(req.getTeamName());
        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            team.setContactDetails(new Team_ContactDetails(
                    req.getPhoneNumber(), req.getAddress()
            ));
        }
        return teamRepo.save(team);
    }

    public Team addPlayer(Long teamId, Long playerId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        team.addPlayer(player);
        return teamRepo.save(team);
    }

    public Team removePlayer(Long teamId, Long playerId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        team.removePlayer(player);
        return teamRepo.save(team);
    }

    public Team setCaptain(Long teamId, Long playerId) {
        Team team = teamRepo.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));
        Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        team.setCaptain(player);
        return teamRepo.save(team);
    }

    public void deleteTeam(Long Id) {
        teamRepo.deleteById(Id);
    }
}