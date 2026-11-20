package com.stephen.Frame;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/frame")
public class Frame_Controller {

    private final Frame_Service frameService;

    public Frame_Controller(Frame_Service frameService) {
        this.frameService = frameService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> publicPing() {
        return ResponseEntity.ok("Frame_Controller is alive!");
    }

    @PostMapping("/singles")
    public ResponseEntity<Frame_Singles> createSingles(@RequestBody Frame_SinglesRequest req) {
        return ResponseEntity.ok(frameService.createSinglesFrame(req));
    }

    @PostMapping("/doubles")
    public ResponseEntity<Frame_Doubles> createDoubles(@RequestBody Frame_DoublesRequest req) {
        return ResponseEntity.ok(frameService.createDoublesFrame(req));
    }

    @PostMapping("/killer")
    public ResponseEntity<Frame_Killer> createKiller(@RequestBody Frame_KillerRequest req) {
        return ResponseEntity.ok(frameService.createKillerFrame(req));
    }

    @PostMapping("/{frameId}/result")
    public ResponseEntity<Frame_Entity> recordResult(
            @PathVariable Long frameId,
            @RequestBody Frame_ResultRequest req) {
        return ResponseEntity.ok(frameService.recordResult(frameId, req));
    }

    /**
     * Records a per-player lives update for a killer frame.
     * Separate endpoint from /result since killer frames use a different
     * request shape (playerId + livesRemaining vs winnerId).
     */
    @PostMapping("/killer/{frameId}/result")
    public ResponseEntity<Frame_Killer> recordKillerResult(
            @PathVariable Long frameId,
            @RequestBody Frame_KillerResultRequest req) {
        return ResponseEntity.ok(frameService.recordKillerResult(frameId, req));
    }

    @GetMapping("/unplayed")
    public ResponseEntity<List<Frame_Entity>> getUnplayed() {
        return ResponseEntity.ok(frameService.getUnplayed());
    }

    @GetMapping("/played")
    public ResponseEntity<List<Frame_Entity>> getPlayed() {
        return ResponseEntity.ok(frameService.getPlayed());
    }
}