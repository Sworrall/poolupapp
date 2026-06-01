package com.stephen.Match;

import java.util.List;

public class Match_StateDTO {

    private Long matchID;
    private String matchType;
    private boolean isPlayed;
    private boolean isDraw;
    private boolean isBye;
    private int frameCount;
    private Long winnerID;
    private Long loserID;
    private List<SlotStateDTO> slots;

    public static class SlotStateDTO {
        private int slotNumber;
        private String status;
        private Long playerAID;
        private String playerAName;
        private Long playerBID;
        private String playerBName;
        private Long frameID;
        private Long frameWinnerID;
        private boolean breakDish;

        // getters & setters
        public int getSlotNumber() { return slotNumber; }
        public void setSlotNumber(int slotNumber) { this.slotNumber = slotNumber; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Long getPlayerAID() { return playerAID; }
        public void setPlayerAID(Long playerAID) { this.playerAID = playerAID; }

        public String getPlayerAName() { return playerAName; }
        public void setPlayerAName(String playerAName) { this.playerAName = playerAName; }

        public Long getPlayerBID() { return playerBID; }
        public void setPlayerBID(Long playerBID) { this.playerBID = playerBID; }

        public String getPlayerBName() { return playerBName; }
        public void setPlayerBName(String playerBName) { this.playerBName = playerBName; }

        public Long getFrameID() { return frameID; }
        public void setFrameID(Long frameID) { this.frameID = frameID; }

        public Long getFrameWinnerID() { return frameWinnerID; }
        public void setFrameWinnerID(Long frameWinnerID) { this.frameWinnerID = frameWinnerID; }

        public boolean isBreakDish() { return breakDish; }
        public void setBreakDish(boolean breakDish) { this.breakDish = breakDish; }
    }

    // getters & setters
    public Long getMatchID() { return matchID; }
    public void setMatchID(Long matchID) { this.matchID = matchID; }

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

    public Long getWinnerID() { return winnerID; }
    public void setWinnerID(Long winnerID) { this.winnerID = winnerID; }

    public Long getLoserID() { return loserID; }
    public void setLoserID(Long loserID) { this.loserID = loserID; }

    public List<SlotStateDTO> getSlots() { return slots; }
    public void setSlots(List<SlotStateDTO> slots) { this.slots = slots; }
}