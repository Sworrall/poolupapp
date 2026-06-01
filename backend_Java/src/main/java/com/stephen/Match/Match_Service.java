package com.stephen.Match;

import com.stephen.Doubles.Doubles;
import com.stephen.Doubles.Doubles_Repository;
import com.stephen.Frame.Doubles.Frame_Doubles;
import com.stephen.Frame.Frame_Repository;
import com.stephen.Frame.Singles.Frame_Singles;
import com.stephen.Match.Doubles.Match_Doubles;
import com.stephen.Match.Doubles.Match_Request_Doubles;
import com.stephen.Match.Singles.Match_Singles;
import com.stephen.Match.Singles.Match_Request_Singles;
import com.stephen.Match.Team.Match_Team;
import com.stephen.Match.Team.Match_Request_Team;
import com.stephen.Player.Player;
import com.stephen.Player.Player_Repository;
import com.stephen.Player.PlayerNotFoundException;
import com.stephen.Team.Team;
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
        Player playerA = playerRepo.findByID(req.getPlayerAID())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerAID()));
        Player playerB = playerRepo.findByID(req.getPlayerBID())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerBID()));

        Match_Singles match;
        if (req.getTeamAID() != null && req.getTeamBID() != null) {
            Team teamA = teamRepo.findByID(req.getTeamAID())
                    .orElseThrow(() -> new TeamNotFoundException(req.getTeamAID()));
            Team teamB = teamRepo.findByID(req.getTeamBID())
                    .orElseThrow(() -> new TeamNotFoundException(req.getTeamBID()));
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
        Doubles doublesA = doublesRepo.findByID(req.getDoublesAID())
                .orElseThrow(() -> new RuntimeException("Doubles not found: " + req.getDoublesAID()));
        Doubles doublesB = doublesRepo.findByID(req.getDoublesBID())
                .orElseThrow(() -> new RuntimeException("Doubles not found: " + req.getDoublesBID()));

        Match_Doubles match = new Match_Doubles(doublesA, doublesB);
        match.setFrameCount(req.getFrameCount());
        matchRepo.save(match);
        createSlots(match, req.getFrameCount());
        return match;
    }

    @Transactional
    public Match_Team createTeamMatch(Match_Request_Team req) {
        Team teamA = teamRepo.findByID(req.getTeamAID())
                .orElseThrow(() -> new TeamNotFoundException(req.getTeamAID()));
        Team teamB = teamRepo.findByID(req.getTeamBID())
                .orElseThrow(() -> new TeamNotFoundException(req.getTeamBID()));

        Match_Team match = new Match_Team(teamA, teamB);
        match.setFrameCount(req.getFrameCount());
        matchRepo.save(match);
        createSlots(match, req.getFrameCount());
        return match;
    }

    private void createSlots(Match match, int frameCount) {
        for (int i = 1; i <= frameCount; i++) {
            slotRepo.save(new Match_Slot(match, i));
        }
    }

    // --- SLOT PLAYER ASSIGNMENT ---
    @Transactional
    public Match_Slot assignPlayerA(Long matchID, int slotNumber, Match_PlayerRequest_Slot req) {
        Match_Slot slot = getSlot(matchID, slotNumber);
        Player player = playerRepo.findByID(req.getPlayerID())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerID()));
        slot.assignPlayerA(player);
        slotRepo.save(slot);
        if (slot.isReady()) createFrameForSlot(slot);
        return slot;
    }

    @Transactional
    public Match_Slot assignPlayerB(Long matchID, int slotNumber, Match_PlayerRequest_Slot req) {
        Match_Slot slot = getSlot(matchID, slotNumber);
        Player player = playerRepo.findByID(req.getPlayerID())
                .orElseThrow(() -> new PlayerNotFoundException(req.getPlayerID()));
        slot.assignPlayerB(player);
        slotRepo.save(slot);
        if (slot.isReady()) createFrameForSlot(slot);
        return slot;
    }

    private void createFrameForSlot(Match_Slot slot) {
        Match match = slot.getMatch();
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
    public void checkAndResolveMatch(Long matchID) {
        Match match = matchRepo.findByID(matchID)
                .orElseThrow(() -> new MatchNotFoundException(matchID));
        List<Match_Slot> slots = slotRepo.findByMatchID(matchID);
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

    private <T> void applyResult(Match match, long winsA, long winsB,
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
    public Optional<Match> getByID(Long ID) {
        return matchRepo.findByID(ID);
    }

    public List<Match_Slot> getSlotsForMatch(Long matchID) {
        return slotRepo.findByMatchID(matchID);
    }

    public List<Match> getUnplayed() {
        return matchRepo.findByIsPlayed(false);
    }

    // --- HELPERS ---
    private Match_Slot getSlot(Long matchID, int slotNumber) {
        return slotRepo.findByMatchIDAndSlotNumber(matchID, slotNumber)
                .orElseThrow(() -> new RuntimeException(
                        "Slot " + slotNumber + " not found for match " + matchID));
    }
}