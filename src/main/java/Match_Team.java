import java.util.*;

public class Match_Team extends Match<Team>{
    private final BaseStats_Key key1;
    private final BaseStats_Key key2;
    private ArrayList<Frame<Team>> frames;
    private final FrameFactory<Team> frameFactory;

    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTOR ---
    public Match_Team(Team teamA, Team teamB, int frameCount, FrameFactory<Team> frameFactory) {
        super(teamA, teamB, frameCount);
        this.key1 = new BaseStats_Key(super.getID(), teamA.getID());
        this.key2 = new BaseStats_Key(super.getID(), teamB.getID());
        this.frameFactory = frameFactory;
        this.isPlayed = false;
        this.isBye = false;
        this.isDraw = false;
        log.info("Created Match_Team: " + teamA.getName() + " vs " + teamB.getName() + " with " + frameCount + " frames.");
    }

    public Match_Team(Team team, int frameCount, FrameFactory<Team> frameFactory) {
        super(team, frameCount);
        this.key1 = new BaseStats_Key(super.getID(), team.getID());
        this.key2 = new BaseStats_Key(super.getID(), 0);
        this.frames = new ArrayList<>();
        this.frameFactory = frameFactory;
        this.isPlayed = false;
        this.isBye = true;
        this.isDraw = false;
        log.info("Created Match_Team with bye: " + team.getName() + " vs BYE with " + frameCount + " frames.");
    }

    public Match_Team(FrameFactory<Team> frameFactory) {
        super(Team.createBye(), Team.createBye(), 0);
        this.key1 = new BaseStats_Key(super.getID(), 0);
        this.key2 = new BaseStats_Key(super.getID(), 0);=
        this.frames = new ArrayList<>();
        this.frameFactory = frameFactory;
        this.isPlayed = false;
        this.isBye = true;
        this.isDraw = false;
        log.info("Created Match_Team with bye: BYE vs BYE with 0 frames.");
    }


    // --- INTERFACE ---
    @Override
    public Team createByeParty() {
        log.info("Creating bye party for Match_Team.");
        return new Team();
    }

    @Override
    public void playMatch(){
        handleByeMatch();
        if(!isBye){
            for (int i = 0; i < this.getFrameCount(); i++) {
                Frame<Team> f = frameFactory.createFrame(party1, party2);
                frames.add(f);
                f.playFrame();
            }
        }
        recordPlayerInTeam_Match();
        recordTeam_Match();
        isPlayed = true;
        log.info("Played Match_Team: " + party1.getName() + " vs " + party2.getName() + ". Result: " + (isDraw ? "Draw" : (getWinner().getName() + " wins")));
    }


    // --- GETTERS ---
    public ArrayList<Frame<Team>>getFrames(){
        log.info("Getting frames for Match_Team: " + party1.getName() + " vs " + party2.getName() + ". Total frames: " + frames.size());
        return this.frames;
    }

    public ArrayList<Player> getParticipantsTeamA(){
        ArrayList<Player> partyList = new ArrayList<>();
        for (Frame<Team> f : this.frames) {
            partyList.add(f.getPlayersA().getFirst());
        }
        log.info("Getting participants for Team A in Match_Team: " + party1.getName() + " vs " + party2.getName() + ". Participants: " + partyList.size());
        return partyList;
    }

    public ArrayList<Player> getParticipantsTeamB(){
        ArrayList<Player> partyList = new ArrayList<>();
        for (Frame<Team> f : this.frames) {
            partyList.add(f.getPlayersB().getFirst());
        }
        log.info("Getting participants for Team B in Match_Team: " + party1.getName() + " vs " + party2.getName() + ". Participants: " + partyList.size());
        return partyList;
    }


    // --- LOGIC ---
    public void recordTeam_Match() {
        BaseStats_Service.applyEvent(key1, StatField.MATCH_TOTAL, getParty1());
        BaseStats_Service.applyEvent(key2, StatField.MATCH_TOTAL, getParty2());
        if (super.isDraw) {
            BaseStats_Service.applyEvent(key1, StatField.MATCH_DRAW, getParty1());
            BaseStats_Service.applyEvent(key2, StatField.MATCH_DRAW, getParty2());
        } else if (this.getWinner().equals(super.party1)) {
            BaseStats_Service.applyEvent(key1, StatField.MATCH_WIN, getParty1());
            BaseStats_Service.applyEvent(key2, StatField.MATCH_LOSS, getParty2());
        } else {
            BaseStats_Service.applyEvent(key1, StatField.MATCH_LOSS, getParty1());
            BaseStats_Service.applyEvent(key2, StatField.MATCH_WIN, getParty2());
        }
        log.info("Recorded team stats for Match_Team: " + party1.getName() + " vs " + party2.getName() + ". Result: " + (isDraw ? "Draw" : (getWinner().getName() + " wins")));
    }

    public void recordPlayerInTeam_Match(){
        for (int i = 0; i < super.getFrameCount(); i++) {
            Frame<Team> f = this.getFrames().get(i);
            Player p1 = f.getPlayersA().getFirst();
            Player p2 = f.getPlayersB().getFirst();
            BaseStats_Key p1Key = new BaseStats_Key(super.getID(), f.getParty1().getID());
            BaseStats_Key p2Key = new BaseStats_Key(super.getID(), f.getParty2().getID());
            BaseStats_Service.applyEvent(p1Key, StatField.MATCH_TOTAL, p1);
            BaseStats_Service.applyEvent(p2Key, StatField.MATCH_TOTAL, p2);
            if (super.isDraw) {
                BaseStats_Service.applyEvent(p1Key, StatField.MATCH_DRAW, p1);
                BaseStats_Service.applyEvent(p2Key, StatField.MATCH_DRAW, p2);
            } else if (super.party1.equals(this.getWinner())) {
                BaseStats_Service.applyEvent(p1Key, StatField.MATCH_WIN, p1);
                BaseStats_Service.applyEvent(p2Key, StatField.MATCH_LOSS, p2);
            } else {
                BaseStats_Service.applyEvent(p1Key, StatField.MATCH_LOSS, p1);
                BaseStats_Service.applyEvent(p2Key, StatField.MATCH_WIN, p2);
            }
        }
        log.info("Recorded player stats for Match_Team: " + party1.getName() + " vs " + party2.getName());
    }
}


