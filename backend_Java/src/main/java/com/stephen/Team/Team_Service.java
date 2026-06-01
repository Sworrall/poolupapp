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
        team.setFirebaseUID(req.getFirebaseUID());
        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            team.setContactDetails(new Team_ContactDetails(
                    req.getPhoneNumber(), req.getAddress()
            ));
        }
        return teamRepo.save(team);
    }

    public Optional<Team> getByID(Long ID) {
        return teamRepo.findByID(ID);
    }

    public List<Team> getAllTeams() {
        return teamRepo.findAll();
    }

    public Team updateTeam(Long ID, Team_Request req) {
        Team team = teamRepo.findByID(ID)
                .orElseThrow(() -> new TeamNotFoundException(ID));
        team.setTeamName(req.getTeamName());
        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            team.setContactDetails(new Team_ContactDetails(
                    req.getPhoneNumber(), req.getAddress()
            ));
        }
        return teamRepo.save(team);
    }

    public Team addPlayer(Long teamID, Long playerID) {
        Team team = teamRepo.findByID(teamID)
                .orElseThrow(() -> new TeamNotFoundException(teamID));
        Player player = playerRepo.findByID(playerID)
                .orElseThrow(() -> new PlayerNotFoundException(playerID));
        team.addPlayer(player);
        return teamRepo.save(team);
    }

    public Team removePlayer(Long teamID, Long playerID) {
        Team team = teamRepo.findByID(teamID)
                .orElseThrow(() -> new TeamNotFoundException(teamID));
        Player player = playerRepo.findByID(playerID)
                .orElseThrow(() -> new PlayerNotFoundException(playerID));
        team.removePlayer(player);
        return teamRepo.save(team);
    }

    public Team setCaptain(Long teamID, Long playerID) {
        Team team = teamRepo.findByID(teamID)
                .orElseThrow(() -> new TeamNotFoundException(teamID));
        Player player = playerRepo.findByID(playerID)
                .orElseThrow(() -> new PlayerNotFoundException(playerID));
        team.setCaptain(player);
        return teamRepo.save(team);
    }

    public void deleteTeam(Long ID) {
        teamRepo.deleteByID(ID);
    }
}