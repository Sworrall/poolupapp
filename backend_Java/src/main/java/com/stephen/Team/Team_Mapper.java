package com.stephen.Team;

import java.util.stream.Collectors;

public class Team_Mapper {

    public static Team_ListResponse toListDTO(Team_Entity team) {
        Team_ListResponse dto = new Team_ListResponse();
        dto.setId(team.getId());
        dto.setTeamName(team.getTeamName());
        dto.setCaptainName(
                team.getCaptain() != null ? team.getCaptain().getName() : null
        );
        dto.setBye(team.isBye());
        return dto;
    }

    public static Team_DetailResponse toDetailDTO(Team_Entity team) {
        Team_DetailResponse dto = new Team_DetailResponse();

        dto.setId(team.getId());
        dto.setTeamName(team.getTeamName());

        if (team.getCaptain() != null) {
            dto.setCaptainId(team.getCaptain().getId());
            dto.setCaptainName(team.getCaptain().getName());
        }

        dto.setPlayers(
                team.getPlayers().stream()
                        .map(p -> {
                            Team_PlayerSummaryResponse pd = new Team_PlayerSummaryResponse();
                            pd.setId(p.getId());
                            pd.setName(p.getName());
                            return pd;
                        })
                        .collect(Collectors.toList())
        );

        if (team.getContactDetails() != null) {
            dto.setAddress(team.getContactDetails().getAddress());
            dto.setPhoneNumber(team.getContactDetails().getPhoneNumber());
        }

        dto.setBye(team.isBye());
        dto.setFirebaseUid(team.getFirebaseUid());
        dto.setCreatedAt(team.getCreatedAt());

        return dto;
    }
}