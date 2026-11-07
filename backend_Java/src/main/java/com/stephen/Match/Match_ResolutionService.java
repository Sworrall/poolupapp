package com.stephen.Match;

import com.stephen.BaseStats.BaseStats_Service;
import com.stephen.Frame.Frame_Doubles;
import com.stephen.Frame.Frame_Singles;
import com.stephen.Tournament.Tournament_Match_Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class Match_ResolutionService {

    private final Tournament_Match_Repository tournamentMatchRepo;
    private final BaseStats_Service baseStats_Service;
    private final Match_Repository matchRepo;
    private final Match_Repository_Slot slotRepo;

    public Match_ResolutionService(Tournament_Match_Repository tournamentMatchRepo, BaseStats_Service baseStatsService, Match_Repository matchRepo,
                                   Match_Repository_Slot slotRepo) {
        this.tournamentMatchRepo = tournamentMatchRepo;
        baseStats_Service = baseStatsService;
        this.matchRepo = matchRepo;
        this.slotRepo = slotRepo;
    }

    @Transactional
    public void checkAndResolveMatch(Long id) {
        Match_Entity match = matchRepo.findById(id)
                .orElseThrow(() -> new MatchNotFoundException(id));

        if (match.isPlayed()) return;               // already resolved: never double-count

        matchRepo.save(match);

        if (match.isPlayed() && !match.isBye()) {   // bye matches have a null party B
            Long tournamentId = tournamentMatchRepo.findByMatchId(id)
                    .map(tm -> tm.getTournament().getId())
                    .orElse(null);
            baseStats_Service.applyMatchResult(match, tournamentId, null);
        }
    }

    private void resolveSingles(Match_Singles match, List<Match_Slot> slots) {
        long winsA = slots.stream()
                .filter(s -> s.getFrame() instanceof Frame_Singles f
                        && f.getWinner() != null
                        && f.getWinner().equals(match.getPlayerA()))
                .count();
        long winsB = slots.stream()
                .filter(s -> s.getFrame() instanceof Frame_Singles f
                        && f.getWinner() != null
                        && f.getWinner().equals(match.getPlayerB()))
                .count();
        applyResult(match, winsA, winsB,
                match.getPlayerA(), match.getPlayerB(),
                match::setWinner, match::setLoser);
    }

    private void resolveDoubles(Match_Doubles match, List<Match_Slot> slots) {
        long winsA = slots.stream()
                .filter(s -> s.getFrame() instanceof Frame_Doubles f
                        && f.getWinner() != null
                        && f.getWinner().equals(match.getDoublesA()))
                .count();
        long winsB = slots.stream()
                .filter(s -> s.getFrame() instanceof Frame_Doubles f
                        && f.getWinner() != null
                        && f.getWinner().equals(match.getDoublesB()))
                .count();
        applyResult(match, winsA, winsB,
                match.getDoublesA(), match.getDoublesB(),
                match::setWinner, match::setLoser);
    }

    private void resolveTeam(Match_Team match, List<Match_Slot> slots) {
        long winsA = slots.stream()
                .filter(s -> s.getFrame() instanceof Frame_Singles f
                        && f.getWinner() != null
                        && match.getTeamA().getPlayers().contains(f.getWinner()))
                .count();
        long winsB = slots.stream()
                .filter(s -> s.getFrame() instanceof Frame_Singles f
                        && f.getWinner() != null
                        && match.getTeamB().getPlayers().contains(f.getWinner()))
                .count();
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
}