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
        dto.setMatchId(match.getId());
        dto.setPlayed(match.isPlayed());
        dto.setDraw(match.isDraw());
        dto.setBye(match.isBye());
        dto.setFrameCount(match.getFrameCount());

        switch (match) {
            case Match_Singles m -> {
                dto.setMatchType("SINGLES");
                if (m.getWinner() != null) dto.setWinnerId(m.getWinner().getId());
                if (m.getLoser() != null) dto.setLoserId(m.getLoser().getId());
            }
            case Match_Doubles m -> {
                dto.setMatchType("DOUBLES");
                if (m.getWinner() != null) dto.setWinnerId(m.getWinner().getId());
                if (m.getLoser() != null) dto.setLoserId(m.getLoser().getId());
            }
            case Match_Team m -> {
                dto.setMatchType("TEAM");
                if (m.getWinner() != null) dto.setWinnerId(m.getWinner().getId());
                if (m.getLoser() != null) dto.setLoserId(m.getLoser().getId());
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
            dto.setPlayerAId(slot.getPlayerA().getId());
            dto.setPlayerAName(slot.getPlayerA().getFullName());
        }
        if (slot.getPlayerB() != null) {
            dto.setPlayerBId(slot.getPlayerB().getId());
            dto.setPlayerBName(slot.getPlayerB().getFullName());
        }
        if (slot.getFrame() != null) {
            dto.setFrameId(slot.getFrame().getId());
            dto.setBreakDish(slot.getFrame().isBreakDish());

            if (slot.getFrame() instanceof Frame_Singles f && f.getWinner() != null) {
                dto.setFrameWinnerId(f.getWinner().getId());
            } else if (slot.getFrame() instanceof Frame_Doubles f && f.getWinner() != null) {
                dto.setFrameWinnerId(f.getWinner().getId());
            }
        }
        return dto;
    }
}