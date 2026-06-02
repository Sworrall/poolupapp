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
            @RequestBody Tournament_RoundRobin_Request request) {
        log.info("POST /tournaments/roundrobin — partyType: {}, parties: {}, frames: {}",
                request.getPartyType(), request.getPartyIds().size(), request.getFrameCount());
        Tournament_RoundRobin tournament = tournamentService.createRoundRobin(request);
        return ResponseEntity.ok(tournament);
    }

    @PostMapping("/ko")
    public ResponseEntity<Tournament_KO> createKO(
            @RequestBody Tournament_KO_Request request) {
        log.info("POST /tournaments/ko — partyType: {}, parties: {}, frames: {}",
                request.getPartyType(), request.getPartyIds().size(), request.getFrameCount());
        Tournament_KO tournament = tournamentService.createKO(request);
        return ResponseEntity.ok(tournament);
    }

    @PostMapping("/killer")
    public ResponseEntity<Tournament_Killer> createKiller(
            @RequestBody Tournament_Killer_Request request) {
        log.info("POST /tournaments/killer — partyType: {}, parties: {}, random: {}",
                request.getPartyType(), request.getPartyIds().size(), request.isRandom());
        Tournament_Killer tournament = tournamentService.createKiller(request);
        return ResponseEntity.ok(tournament);
    }

    @PostMapping("/groupstage")
    public ResponseEntity<Tournament_GroupStage> createGroupStage(
            @RequestBody Tournament_GroupStage_Request request) {
        log.info("POST /tournaments/groupstage — partyType: {}, parties: {}, groups: {}",
                request.getPartyType(), request.getPartyIds().size(), request.getGroupCount());
        Tournament_GroupStage tournament = tournamentService.createGroupStage(request);
        return ResponseEntity.ok(tournament);
    }


    // -------------------------------------------------------------------------
    // READS
    // -------------------------------------------------------------------------

    @GetMapping("/{id}")
    public ResponseEntity<Tournament> getById(@PathVariable Long Id) {
        return ResponseEntity.ok(tournamentService.getById(Id));
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
            @PathVariable Long Id) {
        return ResponseEntity.ok(tournamentService.getFixturesByRound(Id));
    }

    /**
     * Returns fixtures for a specific round or group index.
     */
    @GetMapping("/{id}/fixtures/round/{roundNumber}")
    public ResponseEntity<List<Tournament_Match>> getFixturesForRound(
            @PathVariable Long Id,
            @PathVariable int roundNumber) {
        return ResponseEntity.ok(tournamentService.getFixturesForRound(Id, roundNumber));
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
    public ResponseEntity<Tournament_KO> advanceKORound(@PathVariable Long Id) {
        log.info("POST /tournaments/{}/ko/advance", Id);
        return ResponseEntity.ok(tournamentService.advanceKORound(Id));
    }

    /**
     * Marks a RoundRobin, GroupStage, or Killer tournament complete.
     * Validates all matches are complete before marking.
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Tournament> markComplete(@PathVariable Long Id) {
        log.info("POST /tournaments/{}/complete", Id);
        return ResponseEntity.ok(tournamentService.markComplete(Id));
    }

    /**
     * Checks whether all matches in a tournament are complete.
     */
    @GetMapping("/{id}/complete/check")
    public ResponseEntity<Boolean> checkAllComplete(@PathVariable Long Id) {
        return ResponseEntity.ok(tournamentService.checkAllComplete(Id));
    }


    // -------------------------------------------------------------------------
    // PODIUM
    // -------------------------------------------------------------------------

    /**
     * Sets the podium positions for a tournament manually.
     * Body: ordered list of party Ids [place1, place2, place3, place4].
     */
    @PostMapping("/{id}/positions")
    public ResponseEntity<Tournament> setPositions(
            @PathVariable Long Id,
            @RequestBody List<Long> positions) {
        log.info("POST /tournaments/{}/positions — {}", Id, positions);
        return ResponseEntity.ok(tournamentService.setPositions(Id, positions));
    }


    // -------------------------------------------------------------------------
    // PROMOTE / DEMOTE
    // -------------------------------------------------------------------------

    /**
     * Returns the top N promoted party Ids from a pre-ranked list.
     * The caller supplies the ranked list (from leaderboard/ranking service).
     */
    @PostMapping("/{id}/promote")
    public ResponseEntity<List<Long>> getPromoted(
            @PathVariable Long Id,
            @RequestParam int count,
            @RequestBody List<Long> rankedPartyIds) {
        return ResponseEntity.ok(tournamentService.getPromoted(Id, rankedPartyIds, count));
    }

    /**
     * Returns the bottom N demoted party Ids from a pre-ranked list.
     */
    @PostMapping("/{id}/demote")
    public ResponseEntity<List<Long>> getDemoted(
            @PathVariable Long Id,
            @RequestParam int count,
            @RequestBody List<Long> rankedPartyIds) {
        return ResponseEntity.ok(tournamentService.getDemoted(Id, rankedPartyIds, count));
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
