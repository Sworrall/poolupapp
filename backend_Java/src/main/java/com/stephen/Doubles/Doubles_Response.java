package com.stephen.Doubles;

public record Doubles_Response(
        Long id,
        String teamName,
        Long player1Id,
        String player1Name,
        Long player2Id,
        String player2Name,
        Long captainId,
        String captainName,
        String phoneNumber,
        String address,
        boolean bye
) {

    public static Doubles_Response fromEntity(Doubles_Entity d) {

        return new Doubles_Response(
                d.getId(),
                d.getTeamName(),

                d.getPlayer1() != null ? d.getPlayer1().getId() : null,
                d.getPlayer1() != null ? d.getPlayer1().getName() : null,

                d.getPlayer2() != null ? d.getPlayer2().getId() : null,
                d.getPlayer2() != null ? d.getPlayer2().getName() : null,

                d.getCaptain() != null ? d.getCaptain().getId() : null,
                d.getCaptain() != null ? d.getCaptain().getName() : null,

                d.getContactDetails() != null
                        ? d.getContactDetails().getPhoneNumber()
                        : null,

                d.getContactDetails() != null
                        ? d.getContactDetails().getAddress()
                        : null,

                d.isBye()
        );
    }
}