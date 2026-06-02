package com.stephen.Tournament;

import com.stephen.Doubles.Doubles;
import com.stephen.Doubles.DoublesNotFoundException;
import com.stephen.Doubles.Doubles_Repository;
import com.stephen.Match.Doubles.Match_Doubles;
import com.stephen.Match.Match;
import com.stephen.Match.Match_Repository;
import com.stephen.Match.Singles.Match_Singles;
import com.stephen.Match.Team.Match_Team;
import com.stephen.Player.Player;
import com.stephen.Player.Player_Repository;
import com.stephen.Player.PlayerNotFoundException;
import com.stephen.Team.Team;
import com.stephen.Team.Team_Repository;
import com.stephen.Team.TeamNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class Tournament_Service {

    private static final Logger log = LoggerFactory.getLogger(Tournament_Service.class);

    private final Tournament_Repository tournamentRepository;
    private final Tournament_Match_Repository tournamentMatchRepository;
    private final Match_Repository matchRepository;
    private final Player_Repository playerRepository;
    private final Doubles_Repository doublesRepository;
    private final Team_Repository teamRepository;

    public Tournament_Service(Tournament_Repository tournamentRepository,
                              Tournament_Match_Repository tournamentMatchRepository,
                              Match_Repository matchRepository,
                              Player_Repository playerRepository,
                              Doubles_Repository doublesRepository,
                              Team_Repository teamRepository) {
        this.tournamentRepository = tournamentRepository;
        this.tournamentMatchRepository = tournamentMatchRepository;
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.doublesRepository = doublesRepository;
        this.teamRepository = teamRepository;
    }


    // -------------------------------------------------------------------------
    // CREATION
    // -------------------------------------------------------------------------

    /**
     * Creates a RoundRobin tournament and eagerly generates all fixtures.
     * Every party plays every other party exactly once.
     * Bye party (d = -1L) is injected if the party count is odd.
     */
    @Transactional
    public Tournament_RoundRobin createRoundRobin(Tournament_RoundRobin_Request request) {
        List<Long> partyIds = new ArrayList<>(request.getPartyIds());
        if (partyIds.size() % 2 == 1) {
            partyIds.add(byeId());
            log.info("RoundRobin: odd party count — bye party injected");
        }
        Collections.shuffle(partyIds);

        Tournament_RoundRobin tournament = new Tournament_RoundRobin(
                partyIds, request.getPartyType(), request.getFrameCount());
        tournamentRepository.save(tournament);

        // All pairs — each party plays every other party once
        int sequence = 0;
        for (int i = 0; i < partyIds.size(); i++) {
            for (int j = i + 1; j < partyIds.size(); j++) {
                Match match = createMatch(partyIds.get(i), partyIds.get(j),
                        request.getPartyType(), request.getFrameCount());
                tournamentMatchRepository.save(
                        new Tournament_Match(tournament, match, 0, sequence++));
            }
        }

        log.info("RoundRobin tournament {} created with {} fixtures",
                tournament.getId(), sequence);
        return tournament;
    }

    /**
     * Creates a KO tournament and eagerly scaffolds all rounds upfront.
     * Byes are injected until party count is a power of 2.
     * Bye matches are persisted with status BYE — the winner is the non-bye party.
     */
    @Transactional
    public Tournament_KO createKO(Tournament_KO_Request request) {
        List<Long> partyIds = new ArrayList<>(request.getPartyIds());

        // Pad to next power of 2
        while (!isPowerOf2(partyIds.size())) {
            partyIds.add(byeId());
            log.info("KO: bye injected, size now {}", partyIds.size());
        }

        if (partyIds.size() < 4) {
            log.error("KO tournament requires at least 4 parties, received {}", partyIds.size());
            throw new IllegalArgumentException("KO tournament requires at least 4 parties");
        }

        Collections.shuffle(partyIds);

        Tournament_KO tournament = new Tournament_KO(
                partyIds, request.getPartyType(), request.getFrameCount());
        tournamentRepository.save(tournament);

        // Eagerly scaffold all rounds
        scaffoldKORounds(tournament, partyIds, request.getPartyType(), request.getFrameCount());

        log.info("KO tournament {} created. Rounds: {}", tournament.getId(), getRounds(partyIds.size()));
        return tournament;
    }

    /**
     * Creates a Killer tournament and generates all fixtures.
     * Parties are paired sequentially; odd party count gets a bye.
     */
    @Transactional
    public Tournament_Killer createKiller(Tournament_Killer_Request request) {
        List<Long> partyIds = new ArrayList<>(request.getPartyIds());
        if (partyIds.size() % 2 == 1) {
            partyIds.add(byeId());
            log.info("Killer: odd party count — bye party injected");
        }
        if (request.isRandom()) Collections.shuffle(partyIds);

        Tournament_Killer tournament = new Tournament_Killer(
                partyIds, request.getPartyType(), request.isRandom());
        tournamentRepository.save(tournament);

        // Sequential pairing — party[0] vs party[1], party[2] vs party[3], ...
        for (int i = 0; i < partyIds.size(); i += 2) {
            Match match = createMatch(partyIds.get(i), partyIds.get(i + 1),
                    request.getPartyType(), 1); // Killer: 1 frame per match
            tournamentMatchRepository.save(new Tournament_Match(tournament, match, 0, i / 2));
        }

        log.info("Killer tournament {} created with {} fixtures",
                tournament.getId(), partyIds.size() / 2);
        return tournament;
    }

    /**
     * Creates a GroupStage tournament and generates all group fixtures.
     * Parties are distributed evenly across groups (4 per group minimum).
     * Byes are injected to reach 4 * groupCount if needed.
     */
    @Transactional
    public Tournament_GroupStage createGroupStage(Tournament_GroupStage_Request request) {
        List<Long> partyIds = new ArrayList<>(request.getPartyIds());
        int groupCount = request.getGroupCount();

        if (partyIds.size() < 4) {
            log.error("GroupStage requires at least 4 parties, received {}", partyIds.size());
            throw new IllegalArgumentException("GroupStage requires at least 4 parties");
        }

        // Pad to 4 * groupCount minimum
        while (partyIds.size() < 4 * groupCount) {
            partyIds.add(byeId());
            log.info("GroupStage: bye injected, size now {}", partyIds.size());
        }

        if (request.isRandom()) Collections.shuffle(partyIds);

        Tournament_GroupStage tournament = new Tournament_GroupStage(
                partyIds, request.getPartyType(),
                groupCount, request.getFrameCount(), request.isRandom());
        tournamentRepository.save(tournament);

        // Distribute parties into groups and generate round-robin fixtures per group
        int partiesPerGroup = partyIds.size() / groupCount;
        for (int g = 0; g < groupCount; g++) {
            List<Long> group = partyIds.subList(g * partiesPerGroup, (g + 1) * partiesPerGroup);
            int sequence = 0;
            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    Match match = createMatch(group.get(i), group.get(j),
                            request.getPartyType(), request.getFrameCount());
                    // roundNumber = group index
                    tournamentMatchRepository.save(
                            new Tournament_Match(tournament, match, g, sequence++));
                }
            }
            log.info("GroupStage tournament {}: group {} fixtures generated", tournament.getId(), g);
        }

        log.info("GroupStage tournament {} created. Groups: {}, parties: {}",
                tournament.getId(), groupCount, partyIds.size());
        return tournament;
    }


    // -------------------------------------------------------------------------
    // READS
    // -------------------------------------------------------------------------

    public Tournament getById(Long Id) {
        return tournamentRepository.findById(Id)
                .orElseThrow(() -> new TournamentNotFoundException(Id));
    }

    public List<Tournament> getAll() {
        return tournamentRepository.findAll();
    }

    public List<Tournament> getByPartyType(PartyType partyType) {
        return tournamentRepository.findByPartyType(partyType);
    }

    public List<Tournament> getIncomplete() {
        return tournamentRepository.findByIsCompleteFalse();
    }

    /**
     * Returns all TournamentMatch rows for a tournament, grouped by roundNumber.
     * Reconstructs the original 2D matchList structure for the caller.
     */
    public Map<Integer, List<Tournament_Match>> getFixturesByRound(Long tournamentId) {
        List<Tournament_Match> all = tournamentMatchRepository
                .findByTournamentIdOrderByRoundNumberAscSequenceAsc(tournamentId);
        return all.stream().collect(Collectors.groupingBy(Tournament_Match::getRoundNumber));
    }

    /**
     * Returns fixtures for a specific round/group index.
     */
    public List<Tournament_Match> getFixturesForRound(Long tournamentId, int roundNumber) {
        return tournamentMatchRepository
                .findByTournamentIdAndRoundNumberOrderBySequenceAsc(tournamentId, roundNumber);
    }


    // -------------------------------------------------------------------------
    // KO PROGRESSION
    // -------------------------------------------------------------------------

    /**
     * Advances the KO tournament to the next round.
     * Validates that all matches in the current round are complete before advancing.
     * Since rounds are eagerly scaffolded, this method marks the tournament
     * complete when the final round is done and records the podium.
     */
    @Transactional
    public Tournament_KO advanceKORound(Long tournamentId) {
        Tournament tournament = getById(tournamentId);
        if (!(tournament instanceof Tournament_KO ko)) {
            throw new IllegalArgumentException("Tournament " + tournamentId + " is not a KO tournament");
        }
        if (ko.isComplete()) {
            log.warn("KO tournament {} is already complete", tournamentId);
            return ko;
        }

        Integer currentRound = tournamentMatchRepository.findMaxRoundNumber(tournamentId);
        if (currentRound == null) {
            throw new IllegalStateException("No fixtures found for tournament " + tournamentId);
        }

        // Validate current round is fully complete
        List<Tournament_Match> currentFixtures = tournamentMatchRepository
                .findByTournamentIdAndRoundNumberOrderBySequenceAsc(tournamentId, currentRound);

        boolean allComplete = currentFixtures.stream()
                .map(Tournament_Match::getMatch)
                .allMatch(Match::isPlayed);

        if (!allComplete) {
            log.warn("KO tournament {}: not all matches in round {} are complete", tournamentId, currentRound);
            throw new IllegalStateException("Not all matches in round " + currentRound + " are complete");
        }

        int totalRounds = getRounds(ko.getPartyIds().size());

        if (currentRound == totalRounds - 1) {
            // Final round complete — set podium and mark done
            resolveKOPodium(ko, currentFixtures);
            ko.setComplete(true);
            tournamentRepository.save(ko);
            log.info("KO tournament {} complete. Winner: {}", tournamentId, ko.getPlace1Id());
        } else {
            log.info("KO tournament {}: round {} complete, advancing", tournamentId, currentRound);
        }

        return ko;
    }

    /**
     * Checks whether all matches across the entire tournament are complete.
     * Used by RoundRobin, Killer, and GroupStage to gate podium resolution.
     */
    @Transactional
    public boolean checkAllComplete(Long tournamentId) {
        List<Tournament_Match> all = tournamentMatchRepository
                .findByTournamentIdOrderByRoundNumberAscSequenceAsc(tournamentId);
        return all.stream()
                .map(Tournament_Match::getMatch)
                .allMatch(Match::isPlayed);
    }

    /**
     * Marks a RoundRobin or GroupStage tournament complete if all matches are done.
     * Promote/demote lists are the caller's responsibility (service consumer or controller).
     */
    @Transactional
    public Tournament markComplete(Long tournamentId) {
        Tournament tournament = getById(tournamentId);
        if (!checkAllComplete(tournamentId)) {
            log.warn("Tournament {}: cannot mark complete — unplayed matches remain", tournamentId);
            throw new IllegalStateException("Not all matches are complete for tournament " + tournamentId);
        }
        tournament.setComplete(true);
        tournamentRepository.save(tournament);
        log.info("Tournament {} marked complete", tournamentId);
        return tournament;
    }


    // -------------------------------------------------------------------------
    // PODIUM
    // -------------------------------------------------------------------------

    /**
     * Manually sets the podium for a completed tournament.
     * Consistent with original setPositions() — caller provides ordered list of Ids.
     */
    @Transactional
    public Tournament setPositions(Long tournamentId, List<Long> positions) {
        Tournament tournament = getById(tournamentId);
        tournament.setPositions(positions);
        tournamentRepository.save(tournament);
        log.info("Tournament {} positions set: {}", tournamentId, positions);
        return tournament;
    }


    // -------------------------------------------------------------------------
    // PROMOTE / DEMOTE
    // -------------------------------------------------------------------------

    /**
     * Returns the top N party Ids from a completed RoundRobin or GroupStage tournament.
     * Ordering is determined by the caller supplying a pre-ranked list (from leaderboard/
     * ranking service) — this method slices the front of it.

     * The ranking strategy (Ranking_Points, Ranking_Elimination) is not replicated here;
     * that belongs in a dedicated Leaderboard_Service once migrated.
     */
    public List<Long> getPromoted(Long tournamentId, List<Long> rankedPartyIds, int promoteCount) {
        if (!checkAllComplete(tournamentId)) {
            throw new IllegalStateException("Cannot get promoted parties — not all matches complete");
        }
        if (promoteCount > rankedPartyIds.size()) {
            throw new IllegalArgumentException("promoteCount exceeds number of ranked parties");
        }
        return new ArrayList<>(rankedPartyIds.subList(0, promoteCount));
    }

    public List<Long> getDemoted(Long tournamentId, List<Long> rankedPartyIds, int demoteCount) {
        if (!checkAllComplete(tournamentId)) {
            throw new IllegalStateException("Cannot get demoted parties — not all matches complete");
        }
        if (demoteCount > rankedPartyIds.size()) {
            throw new IllegalArgumentException("demoteCount exceeds number of ranked parties");
        }
        return new ArrayList<>(rankedPartyIds.subList(rankedPartyIds.size() - demoteCount,
                rankedPartyIds.size()));
    }


    // -------------------------------------------------------------------------
    // INTERNAL HELPERS
    // -------------------------------------------------------------------------

    /**
     * Eagerly scaffolds all KO rounds at creation time.
     * Round 0 uses the shuffled party list. Each subsequent round uses
     * placeholder bye matches whose winners will be resolved as real matches complete.

     * Because rounds are eager, the bracket is fully persisted upfront.
     * The advanceKORound() method validates completion rather than generating new rounds.
     */
    private void scaffoldKORounds(Tournament_KO tournament, List<Long> firstRoundParties,
                                  PartyType partyType, int frameCount) {
        List<Long> roundParties = new ArrayList<>(firstRoundParties);
        int round = 0;

        while (roundParties.size() > 1) {
            List<Long> nextRoundParties = new ArrayList<>();
            for (int i = 0; i < roundParties.size(); i += 2) {
                Long p1 = roundParties.get(i);
                Long p2 = roundParties.get(i + 1);
                Match match = createMatch(p1, p2, partyType, frameCount);
                tournamentMatchRepository.save(new Tournament_Match(tournament, match, round, i / 2));

                // Placeholder winner for next round bracket position.
                // For bye matches the winner is the non-bye party immediately.
                // Real matches are resolved via Match/Frame endpoints.
                if (isBye(p1)) {
                    nextRoundParties.add(p2);
                } else if (isBye(p2)) {
                    nextRoundParties.add(p1);
                } else {
                    // Real match — winner TBD. Use a sentinel so bracket position is reserved.
                    nextRoundParties.add(null);
                }
            }
            round++;
            // Filter out nulls for subsequent scaffolding — only bye-resolved parties propagate
            roundParties = nextRoundParties.stream()
                    .filter(Objects::nonNull)
                    .toList();

            // If all next-round slots are TBD (no byes at all), stop scaffolding further rounds
            // Those rounds exist in the DB as empty slots to be filled by advanceKORound()
            if (roundParties.isEmpty()) break;
        }
    }

    /**
     * Resolves podium from the final KO round's match results.
     * place1 = winner of final, place2 = loser of final.
     * place3/place4 from semi-final losers if they exist.

     * Winner/loser Ids are extracted via the match subtype since Match base class
     * only holds the entity reference, not a bare winnerId column.
     */
    private void resolveKOPodium(Tournament_KO tournament, List<Tournament_Match> finalRoundFixtures) {
        if (finalRoundFixtures.size() == 1) {
            Match finalMatch = finalRoundFixtures.getFirst().getMatch();
            tournament.setPlace1Id(extractWinnerId(finalMatch));
            tournament.setPlace2Id(extractLoserId(finalMatch));
        }

        Integer finalRound = tournamentMatchRepository.findMaxRoundNumber(tournament.getId());
        if (finalRound != null && finalRound > 0) {
            List<Tournament_Match> semiFinals = tournamentMatchRepository
                    .findByTournamentIdAndRoundNumberOrderBySequenceAsc(
                            tournament.getId(), finalRound - 1);
            if (semiFinals.size() >= 2) {
                tournament.setPlace3Id(extractLoserId(semiFinals.get(0).getMatch()));
                tournament.setPlace4Id(extractLoserId(semiFinals.get(1).getMatch()));
            }
        }
    }

    /**
     * Extracts the winner Id from any Match subtype.
     * Returns null if the match has no winner set yet.
     */
    private Long extractWinnerId(Match match) {
        return switch (match) {
            case Match_Singles m -> m.getWinner() != null ? m.getWinner().getId() : null;
            case Match_Doubles m -> m.getWinner() != null ? m.getWinner().getId() : null;
            case Match_Team m    -> m.getWinner() != null ? m.getWinner().getId() : null;
            default -> null;
        };
    }

    /**
     * Extracts the loser Id from any Match subtype.
     * Returns null if the match has no loser set yet.
     */
    private Long extractLoserId(Match match) {
        return switch (match) {
            case Match_Singles m -> m.getLoser() != null ? m.getLoser().getId() : null;
            case Match_Doubles m -> m.getLoser() != null ? m.getLoser().getId() : null;
            case Match_Team m    -> m.getLoser() != null ? m.getLoser().getId() : null;
            default -> null;
        };
    }

    /**
     * Resolves two party Ids to entities and creates the correct Match subtype.
     * Bye party (id = -1L) is handled per subtype — the match is flagged isBye
     * and only the non-bye party is set, consistent with bye handling conventions.
     * frameCount is set via setFrameCount() since Match subtypes have no frameCount
     * constructor parameter.
     */
    private Match createMatch(Long party1Id, Long party2Id, PartyType partyType, int frameCount) {
        Match match = switch (partyType) {
            case SINGLES -> {
                Player p1 = isBye(party1Id) ? null : playerRepository.findById(party1Id)
                        .orElseThrow(() -> new PlayerNotFoundException(party1Id));
                Player p2 = isBye(party2Id) ? null : playerRepository.findById(party2Id)
                        .orElseThrow(() -> new PlayerNotFoundException(party2Id));
                Match_Singles m = (p1 == null || p2 == null)
                        ? new Match_Singles(p1 != null ? p1 : p2)
                        : new Match_Singles(p1, p2);
                m.setBye(p1 == null || p2 == null);
                yield m;
            }
            case DOUBLES -> {
                Doubles d1 = isBye(party1Id) ? null : doublesRepository.findById(party1Id)
                        .orElseThrow(() -> new DoublesNotFoundException(party1Id));
                Doubles d2 = isBye(party2Id) ? null : doublesRepository.findById(party2Id)
                        .orElseThrow(() -> new DoublesNotFoundException(party2Id));
                Match_Doubles m = (d1 == null || d2 == null)
                        ? new Match_Doubles(d1 != null ? d1 : d2)
                        : new Match_Doubles(d1, d2);
                m.setBye(d1 == null || d2 == null);
                yield m;
            }
            case TEAM -> {
                Team t1 = isBye(party1Id) ? null : teamRepository.findById(party1Id)
                        .orElseThrow(() -> new TeamNotFoundException(party1Id));
                Team t2 = isBye(party2Id) ? null : teamRepository.findById(party2Id)
                        .orElseThrow(() -> new TeamNotFoundException(party2Id));
                Match_Team m = (t1 == null || t2 == null)
                        ? new Match_Team(t1 != null ? t1 : t2)
                        : new Match_Team(t1, t2);
                m.setBye(t1 == null || t2 == null);
                yield m;
            }
        };
        match.setFrameCount(frameCount);
        return matchRepository.save(match);
    }

    private boolean isPowerOf2(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * Sentinel Id for bye parties. Consistent with convention established in
     * the existing bye handling at service level.
     */
    private Long byeId() {
        return -1L;
    }

    private boolean isBye(Long partyId) {
        return partyId != null && partyId == -1L;
    }

    private int getRounds(int partyCount) {
        int rounds = 0;
        int size = partyCount;
        while (size > 1) {
            rounds++;
            size /= 2;
        }
        return rounds;
    }
}
