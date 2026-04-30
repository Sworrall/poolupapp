import java.util.ArrayList;

public class Frame_Singles <S extends StatHolder<S>> extends Frame<Player>{
    private final BaseStats_Key frameKey;

    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTOR ---
    public Frame_Singles(Player p1, Player p2){
        super(p1, p2);
        this.frameKey = new BaseStats_Key(super.getID(), null);
        log.info("Created new Frame_Singles with ID: " + super.getID() + " and players: " + p1.getName() + " vs " + p2.getName());
    }

    public Frame_Singles(Player p1){
        super(p1, new Player());
        this.frameKey = new BaseStats_Key(super.getID(), null);
        log.info("Created new Frame_Singles with ID: " + super.getID() + " and player: " + p1.getName() + " vs BYE");
    }

    public Frame_Singles(){
        super(new Player(), new Player());
        this.frameKey = new BaseStats_Key(super.getID(), null);
        log.info("Created new Frame_Singles with ID: " + super.getID() + " and players: BYE vs BYE");
    }


    // --- GETTERS ---
    @Override
    public ArrayList<Player> getPlayersA() {
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(this.getParty1());
        log.info("getPlayersA called for Frame ID: " + super.getID() + " returning player: " + this.getParty1().getName());
        return playerList;
    }

    @Override
    public ArrayList<Player> getPlayersB() {
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(this.getParty2());
        log.info("getPlayersB called for Frame ID: " + super.getID() + " returning player: " + this.getParty2().getName());
        return playerList;
    }


    // --- INTERFACE ---
    @Override
    public void PlayOutFrame() {
        this.handleBye(super.getParty1(), super.getParty2());
        this.playFrame();
        log.info("PlayOutFrame called for Frame ID: " + super.getID() + " with players: " + super.getParty1().getName() + " vs " + super.getParty2().getName());
    }

    @Override
    public void recordFrame() {
        recordSingles_Frame();
        log.info("recordFrame called for Frame ID: " + super.getID() + " with players: " + super.getParty1().getName() + " vs " + super.getParty2().getName());
    }


    // --- STATS LOGIC ---
    public void recordSingles_Frame(){
        Player playerA = this.getParty1();
        Player playerB = this.getParty2();
        BaseStats_Service.applyEvent(frameKey, StatField.FRAME_TOTAL, playerA);
        BaseStats_Service.applyEvent(frameKey, StatField.FRAME_TOTAL, playerB);
        BaseStats_Service.applyFrame_WIN_LOSS(frameKey, frameKey, this);
        log.info("recordSingles_Frame called for Frame ID: " + super.getID() + " with players: " + playerA.getName() + " vs " + playerB.getName());
    }
}