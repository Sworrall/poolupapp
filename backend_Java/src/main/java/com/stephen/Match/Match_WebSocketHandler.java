package com.stephen.Match;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class Match_WebSocketHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final Match_Repository matchRepo;
    private final Match_Repository_Slot slotRepo;
    private final Match_StateMapper mapper;

    public Match_WebSocketHandler(SimpMessagingTemplate messagingTemplate,
                                  Match_Repository matchRepo,
                                  Match_Repository_Slot slotRepo,
                                  Match_StateMapper mapper) {
        this.messagingTemplate = messagingTemplate;
        this.matchRepo = matchRepo;
        this.slotRepo = slotRepo;
        this.mapper = mapper;
    }

    @EventListener
    public void onMatchUpdate(Match_UpdateEvent event) {
        Long matchID = event.getMatchID();

        Match match = matchRepo.findByID(matchID)
                .orElseThrow(() -> new MatchNotFoundException(matchID));
        List<Match_Slot> slots = slotRepo.findByMatchID(matchID);

        Match_StateDTO dto = mapper.toDTO(match, slots);

        // broadcast to all subscribers of this match
        messagingTemplate.convertAndSend(
                "/topic/matches/" + matchID,
                dto
        );
    }
}