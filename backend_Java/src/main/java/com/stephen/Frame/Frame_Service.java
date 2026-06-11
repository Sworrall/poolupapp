package com.stephen.Frame;

import com.stephen.Doubles.Doubles_Entity;
import com.stephen.Doubles.Doubles_Repository;
import com.stephen.Match.Match_EventPublisher;
import com.stephen.Match.Match_ResolutionService;
import com.stephen.Match.Match_Repository_Slot;
import com.stephen.Player.Player_Entity;
import com.stephen.Player.Player_Repository;
import com.stephen.Player.PlayerNotFoundException;
import com.stephen.Team.Team_Entity;
import com.stephen.Team.Team_Repository;
import com.stephen.Team.TeamNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class Frame_Service {

    private final Frame_Repository frameRepo;
    private final Player_Repository playerRepo;
    private final Team_Repository teamRepo;
    private final Doubles_Repository doublesRepo;
    private final Match_Repository_Slot slotRepo;
    private final Match_EventPublisher matchEventPublisher;
    private final Match_ResolutionService matchResolutionService;
    private final Frame_KillerLivesRepository killerLivesRepo;

    public Frame_Service(Frame_Repository frameRepo,
                         Player_Repository playerRepo,
                         Team_Repository teamRepo,
                         Doubles_Repository doublesRepo,
                         Match_Repository_Slot slotRepo,
                         Match_EventPublisher matchEventPublisher,
                         Match_ResolutionService matchResolutionService,
                         Frame_KillerLivesRepository killerLivesRepo) {
        this.frameRepo = frameRepo;
        this.playerRepo = playerRepo;
        this.teamRepo = teamRepo;
        this.doublesRepo = doublesRepo;
        this.slotRepo = slotRepo;
        this.matchEventPublisher = matchEventPublisher;
        this.matchResolutionService = matchResolutionService;
        this.killerLivesRepo = killerLivesRepo;
    }

    // --- RECORD RESULT ---
    @Transactional
    public Frame_Entity recordResult(Long frameId, Frame_ResultRequest req) {
        Frame_Entity frame = frameRepo.findById(frameId)
                .orElseThrow(() -> new FrameNotFoundException(frameId));

        switch (frame) {
            case Frame_Singles singles -> recordSinglesResult(singles, req);
            case Frame_Doubles doubles -> recordDoublesResult(doubles, req);
            case Frame_Killer frameKiller -> throw new IllegalArgumentException(
                    "Use POST /frames/killer/{id}/result with Frame_KillerResultRequest for killer frames");
            default -> throw new IllegalStateException("Unknown frame type for id: " + frameId);
        }

        slotRepo.findByFrameId(frameId).ifPresent(slot -> {
            slot.markComplete();
            slotRepo.save(slot);
            Long matchId = slot.getMatch().getMatchId();
            matchResolutionService.checkAndResolveMatch(matchId);
            matchEventPublisher.publishMatchUpdate(matchId);
        });

        return frame;
    }


    // --- SINGLES ---
    public Frame_Singles createSinglesFrame(Frame_SinglesRequest req) {
        Player_Entity playerA = playerRepo.findById(req.getPlayerAid())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerAid()));
        Player_Entity playerB = playerRepo.findById(req.getPlayerBid())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerBid()));

        Frame_Singles frame;
        if (req.getTeamAid() != null && req.getTeamBid() != null) {
            Team_Entity teamA = teamRepo.findById(req.getTeamAid())
                    .orElseThrow(() -> new TeamNotFoundException(req.getTeamAid()));
            Team_Entity teamB = teamRepo.findById(req.getTeamBid())
                    .orElseThrow(() -> new TeamNotFoundException(req.getTeamBid()));
            frame = new Frame_Singles(playerA, playerB, teamA, teamB);
        } else {
            frame = new Frame_Singles(playerA, playerB);
        }

        if (playerA.isBye() || playerB.isBye()) {
            frame.setBye(true);
            frame.setPlayed(true);
            frame.setWinner(playerA.isBye() ? playerB : playerA);
            frame.setLoser(playerA.isBye() ? playerA : playerB);
        }

        return frameRepo.save(frame);
    }

    private void recordSinglesResult(Frame_Singles frame, Frame_ResultRequest req) {
        if (frame.isPlayed()) throw new IllegalStateException("Frame already played");

        Player_Entity winner = req.getWinnerId().equals(frame.getPlayerA().getId())
                ? frame.getPlayerA()
                : frame.getPlayerB();
        Player_Entity loser = winner.equals(frame.getPlayerA())
                ? frame.getPlayerB()
                : frame.getPlayerA();

        frame.setWinner(winner);
        frame.setLoser(loser);
        frame.setBreakDish(req.isBreakDish());
        frame.setPlayed(true);
        frameRepo.save(frame);
    }


    // --- DOUBLES ---
    public Frame_Doubles createDoublesFrame(Frame_DoublesRequest req) {
        Doubles_Entity doublesA = doublesRepo.findById(req.getDoublesAid())
                .orElseThrow(() -> new RuntimeException("Doubles team not found: " + req.getDoublesAid()));
        Doubles_Entity doublesB = doublesRepo.findById(req.getDoublesBid())
                .orElseThrow(() -> new RuntimeException("Doubles team not found: " + req.getDoublesBid()));

        Frame_Doubles frame = new Frame_Doubles(doublesA, doublesB);

        if (doublesA.isBye() || doublesB.isBye()) {
            frame.setBye(true);
            frame.setPlayed(true);
            frame.setWinner(doublesA.isBye() ? doublesB : doublesA);
        }

        return frameRepo.save(frame);
    }

    private void recordDoublesResult(Frame_Doubles frame, Frame_ResultRequest req) {
        if (frame.isPlayed()) throw new IllegalStateException("Frame already played");

        Doubles_Entity winner = req.getWinnerId().equals(frame.getDoublesA().getId())
                ? frame.getDoublesA()
                : frame.getDoublesB();

        frame.setWinner(winner);
        frame.setBreakDish(req.isBreakDish());
        frame.setPlayed(true);
        frameRepo.save(frame);
    }


    // --- KILLER ---

    /**
     * Creates a killer frame and initialises a Frame_KillerLives record
     * for each player at the specified starting lives count.
     */
    @Transactional
    public Frame_Killer createKillerFrame(Frame_KillerRequest req) {
        Frame_Killer frame = new Frame_Killer(req.getStartingLives());
        frameRepo.save(frame);

        for (Long playerId : req.getPlayerIds()) {
            Player_Entity player = playerRepo.findById(playerId)
                    .orElseThrow(() -> new PlayerNotFoundException(playerId));
            killerLivesRepo.save(new Frame_KillerLives(frame, player, req.getStartingLives()));
        }

        return frame;
    }

    /**
     * Records a lives update for one player in a killer frame.
     * When a player reaches 0 lives they are eliminated — the frame is marked
     * played only when a single player remains (last one standing).
     */
    @Transactional
    public Frame_Killer recordKillerResult(Long frameId, Frame_KillerResultRequest req) {
        Frame_Entity frame = frameRepo.findById(frameId)
                .orElseThrow(() -> new FrameNotFoundException(frameId));
        if (!(frame instanceof Frame_Killer killer)) {
            throw new IllegalArgumentException("Frame " + frameId + " is not a killer frame");
        }
        if (killer.isPlayed()) throw new IllegalStateException("Frame already played");

        Frame_KillerLives lives = killerLivesRepo
                .findByFrameIdAndPlayerId(frameId, req.getPlayerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No lives record for player " + req.getPlayerId() + " in frame " + frameId));

        lives.setLivesRemaining(req.getLivesRemaining());
        killerLivesRepo.save(lives);
        killer.setBreakDish(req.isBreakDish());

        // Check if only one player remains with lives > 0
        List<Frame_KillerLives> allLives = killerLivesRepo.findByFrameId(frameId);
        long activePlayers = allLives.stream()
                .filter(l -> l.getLivesRemaining() > 0)
                .count();

        if (activePlayers == 1) {
            killer.setPlayed(true);
            frameRepo.save(killer);

            slotRepo.findByFrameId(frameId).ifPresent(slot -> {
                slot.markComplete();
                slotRepo.save(slot);
                Long matchId = slot.getMatch().getMatchId();
                matchResolutionService.checkAndResolveMatch(matchId);
                matchEventPublisher.publishMatchUpdate(matchId);
            });
        } else {
            frameRepo.save(killer);
        }

        return killer;
    }


    // --- QUERIES ---
    public List<Frame_Entity> getUnplayed() {
        return frameRepo.findByIsPlayed(false);
    }

    public List<Frame_Entity> getPlayed() {
        return frameRepo.findByIsPlayed(true);
    }
}