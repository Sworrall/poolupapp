package com.stephen.Match;

import com.stephen.Doubles.Doubles_Entity;
import com.stephen.Doubles.Doubles_Repository;
import com.stephen.Frame.Doubles.Frame_Doubles;
import com.stephen.Frame.Frame_Repository;
import com.stephen.Frame.Singles.Frame_Singles;
import com.stephen.Match.DTO.Match_Request_Doubles;
import com.stephen.Match.DTO.Match_Request_Singles;
import com.stephen.Match.DTO.Match_Request_Team;
import com.stephen.Player.Player_Entity;
import com.stephen.Player.Player_Repository;
import com.stephen.Player.PlayerNotFoundException;
import com.stephen.Team.Team_Entity;
import com.stephen.Team.Team_Repository;
import com.stephen.Team.TeamNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class Match_Service {

    private final Match_Repository matchRepo;
    private final Match_Repository_Slot slotRepo;
    private final Frame_Repository frameRepo;
    private final Player_Repository playerRepo;
    private final Team_Repository teamRepo;
    private final Doubles_Repository doublesRepo;

    public Match_Service(Match_Repository matchRepo,
                         Match_Repository_Slot slotRepo,
                         Frame_Repository frameRepo,
                         Player_Repository playerRepo,
                         Team_Repository teamRepo,
                         Doubles_Repository doublesRepo) {
        this.matchRepo = matchRepo;
        this.slotRepo = slotRepo;
        this.frameRepo = frameRepo;
        this.playerRepo = playerRepo;
        this.teamRepo = teamRepo;
        this.doublesRepo = doublesRepo;
    }

    // --- CREATE ---
    @Transactional
    public Match_Singles createSinglesMatch(Match_Request_Singles req) {
        Player_Entity playerA = playerRepo.findById(req.getPlayerAId())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerAId()));
        Player_Entity playerB = playerRepo.findById(req.getPlayerBId())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerBId()));

        Match_Singles match;
        if (req.getTeamAId() != null && req.getTeamBId() != null) {
            Team_Entity teamA = teamRepo.findById(req.getTeamAId())
                    .orElseThrow(() -> new TeamNotFoundException(req.getTeamAId()));
            Team_Entity teamB = teamRepo.findById(req.getTeamBId())
                    .orElseThrow(() -> new TeamNotFoundException(req.getTeamBId()));
            match = new Match_Singles(playerA, playerB, teamA, teamB);
        } else {
            match = new Match_Singles(playerA, playerB);
        }
        match.setFrameCount(req.getFrameCount());
        matchRepo.save(match);
        createSlots(match, req.getFrameCount());
        return match;
    }

    @Transactional
    public Match_Doubles createDoublesMatch(Match_Request_Doubles req) {
        Doubles_Entity doublesA = doublesRepo.findById(req.getDoublesAid())
                .orElseThrow(() -> new RuntimeException("Doubles not found: " + req.getDoublesAid()));
        Doubles_Entity doublesB = doublesRepo.findById(req.getDoublesBid())
                .orElseThrow(() -> new RuntimeException("Doubles not found: " + req.getDoublesBid()));

        Match_Doubles match = new Match_Doubles(doublesA, doublesB);
        match.setFrameCount(req.getFrameCount());
        matchRepo.save(match);
        createSlots(match, req.getFrameCount());
        return match;
    }

    @Transactional
    public Match_Team createTeamMatch(Match_Request_Team req) {
        Team_Entity teamA = teamRepo.findById(req.getTeamAid())
                .orElseThrow(() -> new TeamNotFoundException(req.getTeamAid()));
        Team_Entity teamB = teamRepo.findById(req.getTeamBid())
                .orElseThrow(() -> new TeamNotFoundException(req.getTeamBid()));

        Match_Team match = new Match_Team(teamA, teamB);
        match.setFrameCount(req.getFrameCount());
        matchRepo.save(match);
        createSlots(match, req.getFrameCount());
        return match;
    }

    private void createSlots(Match_Entity match, int frameCount) {
        for (int i = 1; i <= frameCount; i++) {
            slotRepo.save(new Match_Slot(match, i));
        }
    }

    // --- SLOT PLAYER ASSIGNMENT ---
    @Transactional
    public Match_Slot assignPlayerA(Long matchId, int slotNumber, Match_PlayerRequest_Slot req) {
        Match_Slot slot = getSlot(matchId, slotNumber);
        Player_Entity player = playerRepo.findById(req.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerId()));
        slot.assignPlayerA(player);
        slotRepo.save(slot);
        if (slot.isReady()) createFrameForSlot(slot);
        return slot;
    }

    @Transactional
    public Match_Slot assignPlayerB(Long matchId, int slotNumber, Match_PlayerRequest_Slot req) {
        Match_Slot slot = getSlot(matchId, slotNumber);
        Player_Entity player = playerRepo.findById(req.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerId()));
        slot.assignPlayerB(player);
        slotRepo.save(slot);
        if (slot.isReady()) createFrameForSlot(slot);
        return slot;
    }

    private void createFrameForSlot(Match_Slot slot) {
        Match_Entity match = slot.getMatch();
        if (match instanceof Match_Team teamMatch) {
            Frame_Singles frame = new Frame_Singles(
                    slot.getPlayerA(),
                    slot.getPlayerB(),
                    teamMatch.getTeamA(),
                    teamMatch.getTeamB()
            );
            frameRepo.save(frame);
            slot.linkFrame(frame);
            slotRepo.save(slot);
        } else if (match instanceof Match_Singles singlesMatch) {
            Frame_Singles frame = new Frame_Singles(
                    slot.getPlayerA(),
                    slot.getPlayerB()
            );
            frameRepo.save(frame);
            slot.linkFrame(frame);
            slotRepo.save(slot);
        }
    }

    // --- MATCH RESOLUTION ---
    @Transactional
    public void checkAndResolveMatch(Long matchId) {
        Match_Entity match = matchRepo.findById(matchId)
                .orElseThrow(() -> new MatchNotFoundException(matchId));
        List<Match_Slot> slots = slotRepo.findByMatchId(matchId);
        boolean allComplete = slots.stream()
                .allMatch(s -> s.isComplete() || s.getStatus() == Match_Slot.Status.BYE);
        if (!allComplete) return;

        switch (match) {
            case Match_Singles singlesMatch -> resolveSinglesMatch(singlesMatch, slots);
            case Match_Doubles doublesMatch -> resolveDoublesMatch(doublesMatch, slots);
            case Match_Team teamMatch -> resolveTeamMatch(teamMatch, slots);
            default -> {
            }
        }
        matchRepo.save(match);
    }

    private void resolveSinglesMatch(Match_Singles match, List<Match_Slot> slots) {
        long winsA = slots.stream()
                .filter(s -> s.getFrame() != null)
                .filter(s -> ((Frame_Singles) s.getFrame()).getWinner() != null &&
                        ((Frame_Singles) s.getFrame()).getWinner().equals(match.getPlayerA()))
                .count();
        long winsB = slots.size() - winsA;
        applyResult(match, winsA, winsB,
                match.getPlayerA(), match.getPlayerB(),
                match::setWinner, match::setLoser);
    }

    private void resolveDoublesMatch(Match_Doubles match, List<Match_Slot> slots) {
        long winsA = slots.stream()
                .filter(s -> s.getFrame() != null)
                .filter(s -> ((Frame_Doubles) s.getFrame()).getWinner() != null &&
                        ((Frame_Doubles) s.getFrame()).getWinner().equals(match.getDoublesA()))
                .count();
        long winsB = slots.size() - winsA;
        applyResult(match, winsA, winsB,
                match.getDoublesA(), match.getDoublesB(),
                match::setWinner, match::setLoser);
    }

    private void resolveTeamMatch(Match_Team match, List<Match_Slot> slots) {
        long winsA = slots.stream()
                .filter(s -> s.getFrame() != null)
                .filter(s -> ((Frame_Singles) s.getFrame()).getWinner() != null &&
                        match.getTeamA().getPlayers().contains(((Frame_Singles) s.getFrame()).getWinner()))
                .count();
        long winsB = slots.size() - winsA;
        applyResult(match, winsA, winsB,
                match.getTeamA(), match.getTeamB(),
                match::setWinner, match::setLoser);
    }

    private <T> void applyResult(Match_Entity match, long winsA, long winsB,
                                 T partyA, T partyB,
                                 java.util.function.Consumer<T> setWinner,
                                 java.util.function.Consumer<T> setLoser) {
        if (winsA > winsB) {
            setWinner.accept(partyA);
            setLoser.accept(partyB);
        } else if (winsB > winsA) {
            setWinner.accept(partyB);
            setLoser.accept(partyA);
        } else {
            match.setDraw(true);
        }
        match.setPlayed(true);
    }

    // --- QUERIES ---
    public Optional<Match_Entity> getById(Long Id) {
        return matchRepo.findById(Id);
    }

    public List<Match_Slot> getSlotsForMatch(Long matchId) {
        return slotRepo.findByMatchId(matchId);
    }

    public List<Match_Entity> getUnplayed() {
        return matchRepo.findByIsPlayed(false);
    }

    // --- HELPERS ---
    private Match_Slot getSlot(Long matchId, int slotNumber) {
        return slotRepo.findByMatchIdAndSlotNumber(matchId, slotNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Slot " + slotNumber + " not found for match " + matchId));
    }
}