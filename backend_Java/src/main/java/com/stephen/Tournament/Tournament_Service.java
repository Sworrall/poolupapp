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
    private final Tournament_Repository_Match tournamentMatchRepository;
    private final Match_Repository matchRepository;
    private final Player_Repository playerRepository;
    private final Doubles_Repository doublesRepository;
    private final Team_Repository teamRepository;

    public Tournament_Service(Tournament_Repository tournamentRepository,
                              Tournament_Repository_Match tournamentMatchRepository,
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
     * Bye party (ID = -1L) is injected if the party count is odd.
     */
    @Transactional
    public Tournament_RoundRobin createRoundRobin(Tournament_Request_RoundRobin request) {
        List<Long> partyIDs = new ArrayList<>(request.getPartyIDs());
        if (partyIDs.size() % 2 == 1) {
            partyIDs.add(byeID());
            log.info("RoundRobin: odd party count — bye party injected");
        }
        Collections.shuffle(partyIDs);

        Tournament_RoundRobin tournament = new Tournament_RoundRobin(
                partyIDs, request.getPartyType(), request.getFrameCount());
        tournamentRepository.save(tournament);

        // All pairs — each party plays every other party once
        int sequence = 0;
        for (int i = 0; i < partyIDs.size(); i++) {
            for (int j = i + 1; j < partyIDs.size(); j++) {
                Match match = createMatch(partyIDs.get(i), partyIDs.get(j),
                        request.getPartyType(), request.getFrameCount());
                tournamentMatchRepository.save(
                        new Tournament_Match(tournament, match, 0, sequence++));
            }
        }

        log.info("RoundRobin tournament {} created with {} fixtures",
                tournament.getID(), sequence);
        return tournament;
    }

    /**
     * Creates a KO tournament and eagerly scaffolds all rounds upfront.
     * Byes are injected until party count is a power of 2.
     * Bye matches are persisted with status BYE — the winner is the non-bye party.
     */
    @Transactional
    public Tournament_KO createKO(Tournament_Request_KO request) {
        List<Long> partyIDs = new ArrayList<>(request.getPartyIDs());

        // Pad to next power of 2
        while (!isPowerOf2(partyIDs.size())) {
            partyIDs.add(byeID());
            log.info("KO: bye injected, size now {}", partyIDs.size());
        }

        if (partyIDs.size() < 4) {
            log.error("KO tournament requires at least 4 parties, received {}", partyIDs.size());
            throw new IllegalArgumentException("KO tournament requires at least 4 parties");
        }

        Collections.shuffle(partyIDs);

        Tournament_KO tournament = new Tournament_KO(
                partyIDs, request.getPartyType(), request.getFrameCount());
        tournamentRepository.save(tournament);

        // Eagerly scaffold all rounds
        scaffoldKORounds(tournament, partyIDs, request.getPartyType(), request.getFrameCount());

        log.info("KO tournament {} created. Rounds: {}", tournament.getID(), getRounds(partyIDs.size()));
        return tournament;
    }

    /**
     * Creates a Killer tournament and generates all fixtures.
     * Parties are paired sequentially; odd party count gets a bye.
     */
    @Transactional
    public Tournament_Killer createKiller(Tournament_Request_Killer request) {
        List<Long> partyIDs = new ArrayList<>(request.getPartyIDs());
        if (partyIDs.size() % 2 == 1) {
            partyIDs.add(byeID());
            log.info("Killer: odd party count — bye party injected");
        }
        if (request.isRandom()) Collections.shuffle(partyIDs);

        Tournament_Killer tournament = new Tournament_Killer(
                partyIDs, request.getPartyType(), request.isRandom());
        tournamentRepository.save(tournament);

        // Sequential pairing — party[0] vs party[1], party[2] vs party[3], ...
        for (int i = 0; i < partyIDs.size(); i += 2) {
            Match match = createMatch(partyIDs.get(i), partyIDs.get(i + 1),
                    request.getPartyType(), 1); // Killer: 1 frame per match
            tournamentMatchRepository.save(new Tournament_Match(tournament, match, 0, i / 2));
        }

        log.info("Killer tournament {} created with {} fixtures",
                tournament.getID(), partyIDs.size() / 2);
        return tournament;
    }

    /**
     * Creates a GroupStage tournament and generates all group fixtures.
     * Parties are distributed evenly across groups (4 per group minimum).
     * Byes are injected to reach 4 * groupCount if needed.
     */
    @Transactional
    public Tournament_GroupStage createGroupStage(Tournament_Request_GroupStage request) {
        List<Long> partyIDs = new ArrayList<>(request.getPartyIDs());
        int groupCount = request.getGroupCount();

        if (partyIDs.size() < 4) {
            log.error("GroupStage requires at least 4 parties, received {}", partyIDs.size());
            throw new IllegalArgumentException("GroupStage requires at least 4 parties");
        }

        // Pad to 4 * groupCount minimum
        while (partyIDs.size() < 4 * groupCount) {
            partyIDs.add(byeID());
            log.info("GroupStage: bye injected, size now {}", partyIDs.size());
        }

        if (request.isRandom()) Collections.shuffle(partyIDs);

        Tournament_GroupStage tournament = new Tournament_GroupStage(
                partyIDs, request.getPartyType(),
                groupCount, request.getFrameCount(), request.isRandom());
        tournamentRepository.save(tournament);

        // Distribute parties into groups and generate round-robin fixtures per group
        int partiesPerGroup = partyIDs.size() / groupCount;
        for (int g = 0; g < groupCount; g++) {
            List<Long> group = partyIDs.subList(g * partiesPerGroup, (g + 1) * partiesPerGroup);
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
            log.info("GroupStage tournament {}: group {} fixtures generated", tournament.getID(), g);
        }

        log.info("GroupStage tournament {} created. Groups: {}, parties: {}",
                tournament.getID(), groupCount, partyIDs.size());
        return tournament;
    }


    // -------------------------------------------------------------------------
    // READS
    // -------------------------------------------------------------------------

    public Tournament getByID(Long ID) {
        return tournamentRepository.findByID(ID)
                .orElseThrow(() -> new TournamentNotFoundException(ID));
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
    public Map<Integer, List<Tournament_Match>> getFixturesByRound(Long tournamentID) {
        List<Tournament_Match> all = tournamentMatchRepository
                .findByTournamentIDOrderByRoundNumberAscSequenceAsc(tournamentID);
        return all.stream().collect(Collectors.groupingBy(Tournament_Match::getRoundNumber));
    }

    /**
     * Returns fixtures for a specific round/group index.
     */
    public List<Tournament_Match> getFixturesForRound(Long tournamentID, int roundNumber) {
        return tournamentMatchRepository
                .findByTournamentIDAndRoundNumberOrderBySequenceAsc(tournamentID, roundNumber);
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
    public Tournament_KO advanceKORound(Long tournamentID) {
        Tournament tournament = getByID(tournamentID);
        if (!(tournament instanceof Tournament_KO ko)) {
            throw new IllegalArgumentException("Tournament " + tournamentID + " is not a KO tournament");
        }
        if (ko.isComplete()) {
            log.warn("KO tournament {} is already complete", tournamentID);
            return ko;
        }

        Integer currentRound = tournamentMatchRepository.findMaxRoundNumber(tournamentID);
        if (currentRound == null) {
            throw new IllegalStateException("No fixtures found for tournament " + tournamentID);
        }

        // Validate current round is fully complete
        List<Tournament_Match> currentFixtures = tournamentMatchRepository
                .findByTournamentIDAndRoundNumberOrderBySequenceAsc(tournamentID, currentRound);

        boolean allComplete = currentFixtures.stream()
                .map(Tournament_Match::getMatch)
                .allMatch(Match::isPlayed);

        if (!allComplete) {
            log.warn("KO tournament {}: not all matches in round {} are complete", tournamentID, currentRound);
            throw new IllegalStateException("Not all matches in round " + currentRound + " are complete");
        }

        int totalRounds = getRounds(ko.getPartyIDs().size());

        if (currentRound == totalRounds - 1) {
            // Final round complete — set podium and mark done
            resolveKOPodium(ko, currentFixtures);
            ko.setComplete(true);
            tournamentRepository.save(ko);
            log.info("KO tournament {} complete. Winner: {}", tournamentID, ko.getPlace1ID());
        } else {
            log.info("KO tournament {}: round {} complete, advancing", tournamentID, currentRound);
        }

        return ko;
    }

    /**
     * Checks whether all matches across the entire tournament are complete.
     * Used by RoundRobin, Killer, and GroupStage to gate podium resolution.
     */
    @Transactional
    public boolean checkAllComplete(Long tournamentID) {
        List<Tournament_Match> all = tournamentMatchRepository
                .findByTournamentIDOrderByRoundNumberAscSequenceAsc(tournamentID);
        return all.stream()
                .map(Tournament_Match::getMatch)
                .allMatch(Match::isPlayed);
    }

    /**
     * Marks a RoundRobin or GroupStage tournament complete if all matches are done.
     * Promote/demote lists are the caller's responsibility (service consumer or controller).
     */
    @Transactional
    public Tournament markComplete(Long tournamentID) {
        Tournament tournament = getByID(tournamentID);
        if (!checkAllComplete(tournamentID)) {
            log.warn("Tournament {}: cannot mark complete — unplayed matches remain", tournamentID);
            throw new IllegalStateException("Not all matches are complete for tournament " + tournamentID);
        }
        tournament.setComplete(true);
        tournamentRepository.save(tournament);
        log.info("Tournament {} marked complete", tournamentID);
        return tournament;
    }


    // -------------------------------------------------------------------------
    // PODIUM
    // -------------------------------------------------------------------------

    /**
     * Manually sets the podium for a completed tournament.
     * Consistent with original setPositions() — caller provides ordered list of IDs.
     */
    @Transactional
    public Tournament setPositions(Long tournamentID, List<Long> positions) {
        Tournament tournament = getByID(tournamentID);
        tournament.setPositions(positions);
        tournamentRepository.save(tournament);
        log.info("Tournament {} positions set: {}", tournamentID, positions);
        return tournament;
    }


    // -------------------------------------------------------------------------
    // PROMOTE / DEMOTE
    // -------------------------------------------------------------------------

    /**
     * Returns the top N party IDs from a completed RoundRobin or GroupStage tournament.
     * Ordering is determined by the caller supplying a pre-ranked list (from leaderboard/
     * ranking service) — this method slices the front of it.

     * The ranking strategy (Ranking_Points, Ranking_Elimination) is not replicated here;
     * that belongs in a dedicated Leaderboard_Service once migrated.
     */
    public List<Long> getPromoted(Long tournamentID, List<Long> rankedPartyIDs, int promoteCount) {
        if (!checkAllComplete(tournamentID)) {
            throw new IllegalStateException("Cannot get promoted parties — not all matches complete");
        }
        if (promoteCount > rankedPartyIDs.size()) {
            throw new IllegalArgumentException("promoteCount exceeds number of ranked parties");
        }
        return new ArrayList<>(rankedPartyIDs.subList(0, promoteCount));
    }

    public List<Long> getDemoted(Long tournamentID, List<Long> rankedPartyIDs, int demoteCount) {
        if (!checkAllComplete(tournamentID)) {
            throw new IllegalStateException("Cannot get demoted parties — not all matches complete");
        }
        if (demoteCount > rankedPartyIDs.size()) {
            throw new IllegalArgumentException("demoteCount exceeds number of ranked parties");
        }
        return new ArrayList<>(rankedPartyIDs.subList(rankedPartyIDs.size() - demoteCount,
                rankedPartyIDs.size()));
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

     * Winner/loser IDs are extracted via the match subtype since Match base class
     * only holds the entity reference, not a bare winnerID column.
     */
    private void resolveKOPodium(Tournament_KO tournament, List<Tournament_Match> finalRoundFixtures) {
        if (finalRoundFixtures.size() == 1) {
            Match finalMatch = finalRoundFixtures.getFirst().getMatch();
            tournament.setPlace1ID(extractWinnerID(finalMatch));
            tournament.setPlace2ID(extractLoserID(finalMatch));
        }

        Integer finalRound = tournamentMatchRepository.findMaxRoundNumber(tournament.getID());
        if (finalRound != null && finalRound > 0) {
            List<Tournament_Match> semiFinals = tournamentMatchRepository
                    .findByTournamentIDAndRoundNumberOrderBySequenceAsc(
                            tournament.getID(), finalRound - 1);
            if (semiFinals.size() >= 2) {
                tournament.setPlace3ID(extractLoserID(semiFinals.get(0).getMatch()));
                tournament.setPlace4ID(extractLoserID(semiFinals.get(1).getMatch()));
            }
        }
    }

    /**
     * Extracts the winner ID from any Match subtype.
     * Returns null if the match has no winner set yet.
     */
    private Long extractWinnerID(Match match) {
        return switch (match) {
            case Match_Singles m -> m.getWinner() != null ? m.getWinner().getID() : null;
            case Match_Doubles m -> m.getWinner() != null ? m.getWinner().getID() : null;
            case Match_Team m    -> m.getWinner() != null ? m.getWinner().getID() : null;
            default -> null;
        };
    }

    /**
     * Extracts the loser ID from any Match subtype.
     * Returns null if the match has no loser set yet.
     */
    private Long extractLoserID(Match match) {
        return switch (match) {
            case Match_Singles m -> m.getLoser() != null ? m.getLoser().getID() : null;
            case Match_Doubles m -> m.getLoser() != null ? m.getLoser().getID() : null;
            case Match_Team m    -> m.getLoser() != null ? m.getLoser().getID() : null;
            default -> null;
        };
    }

    /**
     * Resolves two party IDs to entities and creates the correct Match subtype.
     * Bye party (id = -1L) is handled per subtype — the match is flagged isBye
     * and only the non-bye party is set, consistent with bye handling conventions.
     * frameCount is set via setFrameCount() since Match subtypes have no frameCount
     * constructor parameter.
     */
    private Match createMatch(Long party1ID, Long party2ID, PartyType partyType, int frameCount) {
        Match match = switch (partyType) {
            case SINGLES -> {
                Player p1 = isBye(party1ID) ? null : playerRepository.findByID(party1ID)
                        .orElseThrow(() -> new PlayerNotFoundException(party1ID));
                Player p2 = isBye(party2ID) ? null : playerRepository.findByID(party2ID)
                        .orElseThrow(() -> new PlayerNotFoundException(party2ID));
                Match_Singles m = (p1 == null || p2 == null)
                        ? new Match_Singles(p1 != null ? p1 : p2)
                        : new Match_Singles(p1, p2);
                m.setBye(p1 == null || p2 == null);
                yield m;
            }
            case DOUBLES -> {
                Doubles d1 = isBye(party1ID) ? null : doublesRepository.findByID(party1ID)
                        .orElseThrow(() -> new DoublesNotFoundException(party1ID));
                Doubles d2 = isBye(party2ID) ? null : doublesRepository.findByID(party2ID)
                        .orElseThrow(() -> new DoublesNotFoundException(party2ID));
                Match_Doubles m = (d1 == null || d2 == null)
                        ? new Match_Doubles(d1 != null ? d1 : d2)
                        : new Match_Doubles(d1, d2);
                m.setBye(d1 == null || d2 == null);
                yield m;
            }
            case TEAM -> {
                Team t1 = isBye(party1ID) ? null : teamRepository.findByID(party1ID)
                        .orElseThrow(() -> new TeamNotFoundException(party1ID));
                Team t2 = isBye(party2ID) ? null : teamRepository.findByID(party2ID)
                        .orElseThrow(() -> new TeamNotFoundException(party2ID));
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
     * Sentinel ID for bye parties. Consistent with convention established in
     * the existing bye handling at service level.
     */
    private Long byeID() {
        return -1L;
    }

    private boolean isBye(Long partyID) {
        return partyID != null && partyID == -1L;
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
