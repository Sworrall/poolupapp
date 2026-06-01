package com.stephen.Frame;

import com.stephen.Doubles.Doubles;
import com.stephen.Doubles.Doubles_Repository;
import com.stephen.Frame.Doubles.Frame_Doubles;
import com.stephen.Frame.Doubles.Frame_DoublesRequest;
import com.stephen.Frame.Killer.Frame_Killer;
import com.stephen.Frame.Killer.Frame_KillerLives;
import com.stephen.Frame.Killer.Frame_KillerLivesRepository;
import com.stephen.Frame.Killer.Frame_KillerRequest;
import com.stephen.Frame.Killer.Frame_KillerResultRequest;
import com.stephen.Frame.Singles.Frame_Singles;
import com.stephen.Frame.Singles.Frame_Request_Singles;
import com.stephen.Match.Match_EventPublisher;
import com.stephen.Match.Match_ResolutionService;
import com.stephen.Match.Match_Repository_Slot;
import com.stephen.Player.Player;
import com.stephen.Player.Player_Repository;
import com.stephen.Player.PlayerNotFoundException;
import com.stephen.Team.Team;
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
    public Frame recordResult(Long frameID, Frame_ResultRequest req) {
        Frame frame = frameRepo.findByID(frameID)
                .orElseThrow(() -> new FrameNotFoundException(frameID));

        switch (frame) {
            case Frame_Singles singles -> recordSinglesResult(singles, req);
            case Frame_Doubles doubles -> recordDoublesResult(doubles, req);
            case Frame_Killer frameKiller -> throw new IllegalArgumentException(
                    "Use POST /frames/killer/{id}/result with Frame_KillerResultRequest for killer frames");
            default -> throw new IllegalStateException("Unknown frame type for id: " + frameID);
        }

        slotRepo.findByFrameID(frameID).ifPresent(slot -> {
            slot.markComplete();
            slotRepo.save(slot);
            Long matchID = slot.getMatch().getID();
            matchResolutionService.checkAndResolveMatch(matchID);
            matchEventPublisher.publishMatchUpdate(matchID);
        });

        return frame;
    }


    // --- SINGLES ---
    public Frame_Singles createSinglesFrame(Frame_Request_Singles req) {
        Player playerA = playerRepo.findByID(req.getPlayerAID())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerAID()));
        Player playerB = playerRepo.findByID(req.getPlayerBID())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerBID()));

        Frame_Singles frame;
        if (req.getTeamAID() != null && req.getTeamBID() != null) {
            Team teamA = teamRepo.findByID(req.getTeamAID())
                    .orElseThrow(() -> new TeamNotFoundException(req.getTeamAID()));
            Team teamB = teamRepo.findByID(req.getTeamBID())
                    .orElseThrow(() -> new TeamNotFoundException(req.getTeamBID()));
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

        Player winner = req.getWinnerID().equals(frame.getPlayerA().getID())
                ? frame.getPlayerA()
                : frame.getPlayerB();
        Player loser = winner.equals(frame.getPlayerA())
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
        Doubles doublesA = doublesRepo.findByID(req.getDoublesAID())
                .orElseThrow(() -> new RuntimeException("Doubles team not found: " + req.getDoublesAID()));
        Doubles doublesB = doublesRepo.findByID(req.getDoublesBID())
                .orElseThrow(() -> new RuntimeException("Doubles team not found: " + req.getDoublesBID()));

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

        Doubles winner = req.getWinnerID().equals(frame.getDoublesA().getID())
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

        for (Long playerID : req.getPlayerIDs()) {
            Player player = playerRepo.findByID(playerID)
                    .orElseThrow(() -> new PlayerNotFoundException(playerID));
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
    public Frame_Killer recordKillerResult(Long frameID, Frame_KillerResultRequest req) {
        Frame frame = frameRepo.findByID(frameID)
                .orElseThrow(() -> new FrameNotFoundException(frameID));
        if (!(frame instanceof Frame_Killer killer)) {
            throw new IllegalArgumentException("Frame " + frameID + " is not a killer frame");
        }
        if (killer.isPlayed()) throw new IllegalStateException("Frame already played");

        Frame_KillerLives lives = killerLivesRepo
                .findByFrameIDAndPlayerID(frameID, req.getPlayerID())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No lives record for player " + req.getPlayerID() + " in frame " + frameID));

        lives.setLivesRemaining(req.getLivesRemaining());
        killerLivesRepo.save(lives);
        killer.setBreakDish(req.isBreakDish());

        // Check if only one player remains with lives > 0
        List<Frame_KillerLives> allLives = killerLivesRepo.findByFrameID(frameID);
        long activePlayers = allLives.stream()
                .filter(l -> l.getLivesRemaining() > 0)
                .count();

        if (activePlayers == 1) {
            killer.setPlayed(true);
            frameRepo.save(killer);

            slotRepo.findByFrameID(frameID).ifPresent(slot -> {
                slot.markComplete();
                slotRepo.save(slot);
                Long matchID = slot.getMatch().getID();
                matchResolutionService.checkAndResolveMatch(matchID);
                matchEventPublisher.publishMatchUpdate(matchID);
            });
        } else {
            frameRepo.save(killer);
        }

        return killer;
    }


    // --- QUERIES ---
    public List<Frame> getUnplayed() {
        return frameRepo.findByIsPlayed(false);
    }

    public List<Frame> getPlayed() {
        return frameRepo.findByIsPlayed(true);
    }
}