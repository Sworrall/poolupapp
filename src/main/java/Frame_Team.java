import java.util.ArrayList;

public class Frame_Team <S extends StatHolder<S>> extends Frame<Team> {
    private Player playerA;
    private Player playerB;
    private final BaseStats_Key frameKeyA;
    private final BaseStats_Key frameKeyB;

    private static final Logger log = LoggerFactory.getLogger(Functions.class);

    // --- CONSTRUCTORS ---
    public Frame_Team(Team teamA, Team teamB, Player playerA, Player playerB){
        super(teamA, teamB);
        this.playerA = playerA;
        this.playerB = playerB;
        this.frameKeyA = new BaseStats_Key(super.getID(), teamA.getID());
        this.frameKeyB = new BaseStats_Key(super.getID(), teamB.getID());
        log.info("Frame_Team created with Team A: " + teamA.getName() + " and Team B: " + teamB.getName());
    }

    public Frame_Team(Team team, Player player){
        super(team, new Team());
        this.playerA = player;
        this.playerB = new Player();
        this.frameKeyA = new BaseStats_Key(super.getID(), team.getID());
        this.frameKeyB = new BaseStats_Key(super.getID(), 0);
        log.info("Frame_Team created with Team: " + team.getName() + " and Player: " + player.getName());
    }

    public Frame_Team(){
        super(new Team(), new Team());
        this.frameKeyA = new BaseStats_Key(super.getID(), 0);
        this.frameKeyB = new BaseStats_Key(super.getID(), 0);
        log.info("Frame_Team created with default constructor");
    }


    // --- INTERFACE ---
    @Override
    public void PlayOutFrame() {
        this.handleBye(super.getParty1(), super.getParty2());
        this.playFrame();
        log.info("Frame played out for Frame ID: " + super.getID());
    }

    @Override
    public void recordFrame() {
        recordTeam_Frame();
        recordPlayerInTeam_Frame();
        log.info("Frame recorded for Frame ID: " + super.getID());
    }

    
    // --- GETTERS ---
    @Override
    public ArrayList<Player> getPlayersA(){
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(playerA);
        log.info("Retrieved players for Team A: " + playerA.getName());
        return playerList;
    }

    @Override
    public ArrayList<Player> getPlayersB(){
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(playerB);
        log.info("Retrieved players for Team B: " + playerB.getName());
        return playerList;
    }

    public ArrayList<Player> getPlayers(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(playerA);
        players.add(playerB);
        log.info("Retrieved all players for Frame ID: " + super.getID());
        return players;
    }

    // --- STATS LOGIC ---
    public void recordTeam_Frame(){
        Team teamA = this.getParty1();
        Team teamB = this.getParty2();
        BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_TOTAL, teamA);
        BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_TOTAL, teamB);
        BaseStats_Service.applyFrame_WIN_LOSS(frameKeyA, frameKeyB, this);
        log.info("Recorded team stats for Frame ID: " + super.getID() + " - Team A: " + teamA.getName() + ", Team B: " + teamB.getName());
    }

    public void recordPlayerInTeam_Frame() {
        Player playerA = this.playerA;
        Player playerB = this.playerB;
        BaseStats_Service.applyEvent(frameKeyA, StatField.FRAME_TOTAL, playerA);
        BaseStats_Service.applyEvent(frameKeyB, StatField.FRAME_TOTAL, playerB);
        BaseStats_Service.applyFrame_WIN_LOSS(frameKeyA, frameKeyB, this);
        log.info("Recorded player stats for Frame ID: " + super.getID() + " - Player A: " + playerA.getName() + ", Player B: " + playerB.getName());
    }
}
