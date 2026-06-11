package com.stephen.Team;

import com.stephen.Player.PlayerNotFoundException;
import com.stephen.Player.Player_Entity;
import com.stephen.Player.Player_Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Team_Service {

    private final Team_Repository teamRepo;
    private final Player_Repository playerRepo;

    public Team_Service(Team_Repository teamRepo, Player_Repository playerRepo) {
        this.teamRepo = teamRepo;
        this.playerRepo = playerRepo;
    }

    public Team_DetailResponse createTeam(Team_Request req) {
        Team_Entity team = new Team_Entity();
        team.setTeamName(req.getTeamName());
        team.setFirebaseUid(req.getFirebaseUid());

        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            team.setContactDetails(
                    new Team_ContactDetails(req.getPhoneNumber(), req.getAddress())
            );
        }

        return Team_Mapper.toDetailDTO(teamRepo.save(team));
    }

    public List<Team_ListResponse> getAllTeams() {
        return teamRepo.findAll()
                .stream()
                .map(Team_Mapper::toListDTO)
                .collect(Collectors.toList());
    }

    public Team_DetailResponse getById(Long id) {
        Team_Entity team = teamRepo.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));

        return Team_Mapper.toDetailDTO(team);
    }

    public Team_DetailResponse updateTeam(Long id, Team_Request req) {
        Team_Entity team = teamRepo.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));

        team.setTeamName(req.getTeamName());

        if (req.getPhoneNumber() != null || req.getAddress() != null) {
            team.setContactDetails(
                    new Team_ContactDetails(req.getPhoneNumber(), req.getAddress())
            );
        }

        return Team_Mapper.toDetailDTO(teamRepo.save(team));
    }

    public Team_DetailResponse addPlayer(Long teamId, Long playerId) {
        Team_Entity team = teamRepo.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        Player_Entity player = playerRepo.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        team.addPlayer(player);

        return Team_Mapper.toDetailDTO(teamRepo.save(team));
    }

    public Team_DetailResponse removePlayer(Long teamId, Long playerId) {
        Team_Entity team = teamRepo.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        Player_Entity player = playerRepo.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        team.removePlayer(player);

        return Team_Mapper.toDetailDTO(teamRepo.save(team));
    }

    public Team_DetailResponse setCaptain(Long teamId, Long playerId) {
        Team_Entity team = teamRepo.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        Player_Entity player = playerRepo.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        team.setCaptain(player);

        return Team_Mapper.toDetailDTO(teamRepo.save(team));
    }

    public void deleteTeam(Long id) {
        teamRepo.deleteById(id);
    }
}