package com.stephen.Tournament;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tournaments")
public class Tournament_Controller {

    private static final Logger log = LoggerFactory.getLogger(Tournament_Controller.class);

    private final Tournament_Service tournamentService;

    public Tournament_Controller(Tournament_Service tournamentService) {
        this.tournamentService = tournamentService;
    }


    // -------------------------------------------------------------------------
    // CREATION
    // -------------------------------------------------------------------------

    @PostMapping("/roundrobin")
    public ResponseEntity<Tournament_RoundRobin> createRoundRobin(
            @RequestBody Tournament_Request_RoundRobin request) {
        log.info("POST /tournaments/roundrobin — partyType: {}, parties: {}, frames: {}",
                request.getPartyType(), request.getPartyIDs().size(), request.getFrameCount());
        Tournament_RoundRobin tournament = tournamentService.createRoundRobin(request);
        return ResponseEntity.ok(tournament);
    }

    @PostMapping("/ko")
    public ResponseEntity<Tournament_KO> createKO(
            @RequestBody Tournament_Request_KO request) {
        log.info("POST /tournaments/ko — partyType: {}, parties: {}, frames: {}",
                request.getPartyType(), request.getPartyIDs().size(), request.getFrameCount());
        Tournament_KO tournament = tournamentService.createKO(request);
        return ResponseEntity.ok(tournament);
    }

    @PostMapping("/killer")
    public ResponseEntity<Tournament_Killer> createKiller(
            @RequestBody Tournament_Request_Killer request) {
        log.info("POST /tournaments/killer — partyType: {}, parties: {}, random: {}",
                request.getPartyType(), request.getPartyIDs().size(), request.isRandom());
        Tournament_Killer tournament = tournamentService.createKiller(request);
        return ResponseEntity.ok(tournament);
    }

    @PostMapping("/groupstage")
    public ResponseEntity<Tournament_GroupStage> createGroupStage(
            @RequestBody Tournament_Request_GroupStage request) {
        log.info("POST /tournaments/groupstage — partyType: {}, parties: {}, groups: {}",
                request.getPartyType(), request.getPartyIDs().size(), request.getGroupCount());
        Tournament_GroupStage tournament = tournamentService.createGroupStage(request);
        return ResponseEntity.ok(tournament);
    }


    // -------------------------------------------------------------------------
    // READS
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getByID(@PathVariable Long ID) {
        return ResponseEntity.ok(tournamentService.getByID(ID));
    }

    @GetMapping
    public ResponseEntity<List<Tournament>> getAll() {
        return ResponseEntity.ok(tournamentService.getAll());
    }

    @GetMapping("/party-type/{partyType}")
    public ResponseEntity<List<Tournament>> getByPartyType(
            @PathVariable PartyType partyType) {
        return ResponseEntity.ok(tournamentService.getByPartyType(partyType));
    }

    @GetMapping("/incomplete")
    public ResponseEntity<List<Tournament>> getIncomplete() {
        return ResponseEntity.ok(tournamentService.getIncomplete());
    }

    /**
     * Returns all fixtures for a tournament, grouped by round/group index.
     * Reconstructs the original matchList structure for the client.
     */
    @GetMapping("/{id}/fixtures")
    public ResponseEntity<Map<Integer, List<Tournament_Match>>> getFixtures(
            @PathVariable Long ID) {
        return ResponseEntity.ok(tournamentService.getFixturesByRound(ID));
    }

    /**
     * Returns fixtures for a specific round or group index.
     */
    @GetMapping("/{id}/fixtures/round/{roundNumber}")
    public ResponseEntity<List<Tournament_Match>> getFixturesForRound(
            @PathVariable Long ID,
            @PathVariable int roundNumber) {
        return ResponseEntity.ok(tournamentService.getFixturesForRound(ID, roundNumber));
    }


    // -------------------------------------------------------------------------
    // PROGRESSION
    // -------------------------------------------------------------------------

    /**
     * Advances a KO tournament to the next round.
     * Validates all current round matches are complete before advancing.
     * Marks the tournament complete and resolves the podium when the final round is done.
     */
    @PostMapping("/{id}/ko/advance")
    public ResponseEntity<Tournament_KO> advanceKORound(@PathVariable Long ID) {
        log.info("POST /tournaments/{}/ko/advance", ID);
        return ResponseEntity.ok(tournamentService.advanceKORound(ID));
    }

    /**
     * Marks a RoundRobin, GroupStage, or Killer tournament complete.
     * Validates all matches are complete before marking.
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Tournament> markComplete(@PathVariable Long ID) {
        log.info("POST /tournaments/{}/complete", ID);
        return ResponseEntity.ok(tournamentService.markComplete(ID));
    }

    /**
     * Checks whether all matches in a tournament are complete.
     */
    @GetMapping("/{id}/complete/check")
    public ResponseEntity<Boolean> checkAllComplete(@PathVariable Long ID) {
        return ResponseEntity.ok(tournamentService.checkAllComplete(ID));
    }


    // -------------------------------------------------------------------------
    // PODIUM
    // -------------------------------------------------------------------------

    /**
     * Sets the podium positions for a tournament manually.
     * Body: ordered list of party IDs [place1, place2, place3, place4].
     */
    @PostMapping("/{id}/positions")
    public ResponseEntity<Tournament> setPositions(
            @PathVariable Long ID,
            @RequestBody List<Long> positions) {
        log.info("POST /tournaments/{}/positions — {}", ID, positions);
        return ResponseEntity.ok(tournamentService.setPositions(ID, positions));
    }


    // -------------------------------------------------------------------------
    // PROMOTE / DEMOTE
    // -------------------------------------------------------------------------

    /**
     * Returns the top N promoted party IDs from a pre-ranked list.
     * The caller supplies the ranked list (from leaderboard/ranking service).
     */
    @PostMapping("/{id}/promote")
    public ResponseEntity<List<Long>> getPromoted(
            @PathVariable Long ID,
            @RequestParam int count,
            @RequestBody List<Long> rankedPartyIDs) {
        return ResponseEntity.ok(tournamentService.getPromoted(ID, rankedPartyIDs, count));
    }

    /**
     * Returns the bottom N demoted party IDs from a pre-ranked list.
     */
    @PostMapping("/{id}/demote")
    public ResponseEntity<List<Long>> getDemoted(
            @PathVariable Long ID,
            @RequestParam int count,
            @RequestBody List<Long> rankedPartyIDs) {
        return ResponseEntity.ok(tournamentService.getDemoted(ID, rankedPartyIDs, count));
    }


    // -------------------------------------------------------------------------
    // EXCEPTION HANDLING
    // -------------------------------------------------------------------------

    @ExceptionHandler(TournamentNotFoundException.class)
    public ResponseEntity<String> handleNotFound(TournamentNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }
}
