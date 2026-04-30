
public abstract class Match <S extends StatHolder<S>> extends ID {
    protected final S party1;
    protected final S party2;
    private final int frameCount;
    protected S winner;
    protected S loser;
    protected boolean isPlayed;
    protected boolean isBye;
    protected boolean isDraw;

    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTOR ---
    public Match(S p1, S p2, int frameCount) {
        super();
        this.party1 = p1;
        this.party2 = p2;
        this.frameCount = frameCount;
        this.isBye = false;
        this.isPlayed = false;
        this.isDraw = false;
        log.info("Match created: " + errorCapture());
    }


    public Match(S p1, int frameCount) {
        super();
        this.party1 = p1;
        this.party2 = p1.createByeParty();
        this.frameCount = frameCount;
        this.isBye = true;
        this.isPlayed = false;
        this.isDraw = false;
        log.info("Match created: " + errorCapture());
    }


    // --- ABSTRACT METHODS ---
    public abstract void playMatch();

    public abstract S createByeParty();


    // --- FACTORY ---
    public boolean isByeMatch(){
        handleByeMatch();
        return this.isBye;
        log.info("Bye match detected: " + errorCapture());
    }


    // --- GETTERS ---
    public S getParty1() {
        log.info("Getting party1: " + party1.getName());
        return this.party1;
    }

    public S getParty2() {
        log.info("Getting party2: " + party2.getName());
        return this.party2;
    }

    public int getFrameCount() {
        log.info("Getting frame count: " + frameCount);
        return this.frameCount;
    }

    public boolean isPlayed() {
        log.info("Checking if match is played: " + errorCapture() + " - " + isPlayed);
        return isPlayed;
    }

    public boolean isDraw(){
        log.info("Checking if match is a draw: " + errorCapture() + " - " + isDraw);
        if(this.isPlayed){
            return this.isDraw;
        }else{
            log.error("Match is unplayed");
            throw new IllegalArgumentException("Match is unplayed");
        }
    }

    public boolean isBye(){
        log.info("Checking if match is a bye: " + errorCapture() + " - " + isBye);
        return isBye;
    }

    public S getWinner(){
        log.info("Getting winner: " + errorCapture());
        if(this.isPlayed){
            return this.winner;
        }else{
            log.error("Match is unplayed");
            throw new IllegalArgumentException("Match is unplayed");
        }
    }

    public S getLoser(){
        log.info("Getting loser: " + errorCapture());
        if(this.isPlayed){
            return this.loser;
        }else{
            log.error("Match is unplayed");
            throw new IllegalArgumentException("Match is unplayed");
        }
    }


    // --- LOGIC ---
    public void handleByeMatch(){
        if(party1.isBye() && party2.isBye()){
            isBye = true;
            isPlayed = true;
        }else if (party1.isBye() || party2.isBye()) {
            isBye = true;
            isPlayed = true;
            if (party1.isBye()) {
                winner = party2;
                isPlayed = true;
            } else if (party2.isBye()) {
                winner = party1;
                isPlayed = true;
            }
        }
        log.info("Handled bye match: " + errorCapture() + " - isBye: " + isBye + ", isPlayed: " + isPlayed);
    }


    // --- MISC ---
    public String errorCapture(){
        return party1.getName() + " VS " + party2.getName();
    }
}