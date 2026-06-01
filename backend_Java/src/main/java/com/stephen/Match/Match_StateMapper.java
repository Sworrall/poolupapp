package com.stephen.Match;

import com.stephen.Frame.Singles.Frame_Singles;
import com.stephen.Frame.Doubles.Frame_Doubles;
import com.stephen.Match.Doubles.Match_Doubles;
import com.stephen.Match.Singles.Match_Singles;
import com.stephen.Match.Team.Match_Team;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class Match_StateMapper {

    public Match_StateDTO toDTO(Match match, List<Match_Slot> slots) {
        Match_StateDTO dto = new Match_StateDTO();
        dto.setMatchID(match.getID());
        dto.setPlayed(match.isPlayed());
        dto.setDraw(match.isDraw());
        dto.setBye(match.isBye());
        dto.setFrameCount(match.getFrameCount());

        switch (match) {
            case Match_Singles m -> {
                dto.setMatchType("SINGLES");
                if (m.getWinner() != null) dto.setWinnerID(m.getWinner().getID());
                if (m.getLoser() != null) dto.setLoserID(m.getLoser().getID());
            }
            case Match_Doubles m -> {
                dto.setMatchType("DOUBLES");
                if (m.getWinner() != null) dto.setWinnerID(m.getWinner().getID());
                if (m.getLoser() != null) dto.setLoserID(m.getLoser().getID());
            }
            case Match_Team m -> {
                dto.setMatchType("TEAM");
                if (m.getWinner() != null) dto.setWinnerID(m.getWinner().getID());
                if (m.getLoser() != null) dto.setLoserID(m.getLoser().getID());
            }
            default -> {
            }
        }

        dto.setSlots(slots.stream().map(this::toSlotDTO).toList());
        return dto;
    }

    private Match_StateDTO.SlotStateDTO toSlotDTO(Match_Slot slot) {
        Match_StateDTO.SlotStateDTO dto = new Match_StateDTO.SlotStateDTO();
        dto.setSlotNumber(slot.getSlotNumber());
        dto.setStatus(slot.getStatus().name());

        if (slot.getPlayerA() != null) {
            dto.setPlayerAID(slot.getPlayerA().getID());
            dto.setPlayerAName(slot.getPlayerA().getFullName());
        }
        if (slot.getPlayerB() != null) {
            dto.setPlayerBID(slot.getPlayerB().getID());
            dto.setPlayerBName(slot.getPlayerB().getFullName());
        }
        if (slot.getFrame() != null) {
            dto.setFrameID(slot.getFrame().getID());
            dto.setBreakDish(slot.getFrame().isBreakDish());

            if (slot.getFrame() instanceof Frame_Singles f && f.getWinner() != null) {
                dto.setFrameWinnerID(f.getWinner().getID());
            } else if (slot.getFrame() instanceof Frame_Doubles f && f.getWinner() != null) {
                dto.setFrameWinnerID(f.getWinner().getID());
            }
        }
        return dto;
    }
}