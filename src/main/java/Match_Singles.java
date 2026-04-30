import java.util.ArrayList;

public class Match_Singles extends Match<Player>{
    public BaseStats_Key matchKey;
    private ArrayList<Frame<Player>> frames;
    private final FrameFactory<Player> frameFactory;

    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTOR ---
    public Match_Singles(Player p1, Player p2, int frameCount, FrameFactory<Player> frameFactory) {
        super(p1, p2, frameCount);
        this.matchKey = new BaseStats_Key(super.getID(), null);
        this.frames = new ArrayList<>();
        this.frameFactory = frameFactory;
        super.isPlayed = false;
        super.isBye = false;
        super.isDraw = false;
        log.info("Created Match_Singles: " + p1.getName() + " vs " + p2.getName() + " with " + frameCount + " frames.");
    }

    public Match_Singles(Player p, int frameCount, FrameFactory<Player> frameFactory){
        super(p, frameCount);
        this.matchKey = new BaseStats_Key(super.getID(), null);
        this.frames = new ArrayList<>();
        this.frameFactory = frameFactory;
        super.isPlayed = false;
        super.isBye = true;
        super.isDraw = false;
        log.info("Created Match_Singles: (Bye vs " + p.getName() + ")");
    }

    public Match_Singles(FrameFactory<Player> frameFactory){
        super(Player.createBye(), Player.createBye(), 0);
        this.matchKey = new BaseStats_Key(super.getID(), null);
        this.frames = new ArrayList<>();
        this.frameFactory = frameFactory;
        this.isPlayed = true;
        this.isBye = true;
        this.isDraw = false;
        log.info("Created Match_Singles (Bye vs Bye)");
    }


    // --- INTERFACE ---
    @Override
    public void playMatch(){
        handleByeMatch();
        if(!isBye){
            for (int i = 0; i < this.getFrameCount(); i++) {
                Frame<Player> f = frameFactory.createFrame(party1, party2);
                frames.add(f);
                f.playFrame();
            }
        }
        recordPlayer_Match(this);
        isPlayed = true;
        log.info("Played Match_Singles: " + party1.getName() + " vs " + party2.getName() + ". Result: " + (isDraw ? "Draw" : (getWinner().getName() + " wins")));
    }

    @Override
    public Player createByeParty() {
        log.info("Creating bye party for Match_Singles");
        return new Player();
    }


    // --- GETTERS ---
    public int getTotalFrames(){
        log.info("Getting total frames for Match_Singles: " + getFrameCount());
        return super.getFrameCount();
    }


    // --- LOGIC ---
    public void recordPlayer_Match(Match<Player> m) {
        Player p1 = m.getParty1();
        Player p2 = m.getParty2();
        BaseStats_Service.applyEvent(matchKey, StatField.MATCH_TOTAL, p1);
        BaseStats_Service.applyEvent(matchKey, StatField.MATCH_TOTAL, p2);
        if(this.isPlayed()){
            if (super.isDraw()) {
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_DRAW, p1);
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_DRAW, p2);
            } else if (this.getWinner().equals(super.getParty1())) {
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_WIN, p1);
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_LOSS, p2);
            } else {
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_LOSS, p1);
                BaseStats_Service.applyEvent(matchKey, StatField.MATCH_WIN, p2);
            }
        }
        log.info("Recorded player match stats for Match_Singles: " + p1.getName() + " vs " + p2.getName());
    }
}
