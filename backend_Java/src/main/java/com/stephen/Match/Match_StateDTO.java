package com.stephen.Match;

import java.util.List;

public class Match_StateDTO {

    private Long matchId;
    private String matchType;
    private boolean isPlayed;
    private boolean isDraw;
    private boolean isBye;
    private int frameCount;
    private Long winnerId;
    private Long loserId;
    private List<SlotStateDTO> slots;

    public static class SlotStateDTO {
        private int slotNumber;
        private String status;
        private Long playerAId;
        private String playerAName;
        private Long playerBId;
        private String playerBName;
        private Long frameId;
        private Long frameWinnerId;
        private boolean breakDish;

        // getters & setters
        public int getSlotNumber() { return slotNumber; }
        public void setSlotNumber(int slotNumber) { this.slotNumber = slotNumber; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Long getPlayerAId() { return playerAId; }
        public void setPlayerAId(Long playerAId) { this.playerAId = playerAId; }

        public String getPlayerAName() { return playerAName; }
        public void setPlayerAName(String playerAName) { this.playerAName = playerAName; }

        public Long getPlayerBId() { return playerBId; }
        public void setPlayerBId(Long playerBId) { this.playerBId = playerBId; }

        public String getPlayerBName() { return playerBName; }
        public void setPlayerBName(String playerBName) { this.playerBName = playerBName; }

        public Long getFrameId() { return frameId; }
        public void setFrameId(Long frameId) { this.frameId = frameId; }

        public Long getFrameWinnerId() { return frameWinnerId; }
        public void setFrameWinnerId(Long frameWinnerId) { this.frameWinnerId = frameWinnerId; }

        public boolean isBreakDish() { return breakDish; }
        public void setBreakDish(boolean breakDish) { this.breakDish = breakDish; }
    }

    // getters & setters
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long id) { this.matchId = id; }

    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }

    public boolean isPlayed() { return isPlayed; }
    public void setPlayed(boolean played) { isPlayed = played; }

    public boolean isDraw() { return isDraw; }
    public void setDraw(boolean draw) { isDraw = draw; }

    public boolean isBye() { return isBye; }
    public void setBye(boolean bye) { isBye = bye; }

    public int getFrameCount() { return frameCount; }
    public void setFrameCount(int frameCount) { this.frameCount = frameCount; }

    public Long getWinnerId() { return winnerId; }
    public void setWinnerId(Long id) { this.winnerId = id; }

    public Long getLoserId() { return loserId; }
    public void setLoserId(Long id) { this.loserId = id; }

    public List<SlotStateDTO> getSlots() { return slots; }
    public void setSlots(List<SlotStateDTO> slots) { this.slots = slots; }
}