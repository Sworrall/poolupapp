import java.util.*;

public class Doubles extends ID implements StatHolder<Doubles>{
    private final int GLOBAL = 0;
    private String teamName;
    private Player captain;
    private Team_ContactDetails contactDetails;
    private final ArrayList<Player> players;
    private final Map<BaseStats_Key, BaseStats> stats;
    private final boolean isBye;

    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTORS ---
    public Doubles(String teamName) {
        super();
        this.teamName = Objects.requireNonNull(teamName, "Team name cannot be null");
        this.players = new ArrayList<>();
        this.contactDetails = new Team_ContactDetails();
        this.isBye = false;
        this.stats = new HashMap<>();
        BaseStats_Key K = new BaseStats_Key(GLOBAL, super.getID());
        stats.computeIfAbsent(K, _ -> new BaseStats());
        log.info("Doubles team created: " + teamName);
    }

    public Doubles() {
        super();
        this.teamName = "Bye";
        this.players = new ArrayList<>();
        this.stats = new HashMap<>();
        this.captain = null;
        this.isBye = true;
        log.info("Bye Doubles team created");
    }


    // --- FACTORY ---
    public static Doubles createBye(){
        log.info("Creating Bye Doubles team using factory method");
        return new Doubles();
    }


    // --- GETTERS ---
    @Override
    public String getName() {
        log.info("Getting team name: " + teamName);
        return this.teamName;
    }

    @Override
    public boolean isBye() {
        log.info("Checking if team" + this.teamName + " is Bye: " + isBye);
        return isBye;
    }

    @Override
    public BaseStats getOrCreateStats(BaseStats_Key K) {
        log.info("Getting or creating stats for team " + teamName + " with key: " + K);
        return getOrCreateTeamStats(K);
    }

    @Override
    public Doubles createByeParty() {
        log.info("Creating Bye Doubles team using createByeParty");
        return new Doubles();
    }


    // -- GETTERS ---
    public String getDoublesName() {
        log.info("Getting team name: " + teamName);
        return teamName;
    }

    public ArrayList<Player> getPlayers() {
        log.info("Getting players for team " + teamName + ": " + players);
        return this.players;
    }

    public Player getPlayer(int id) {
        log.info("Getting player with ID " + id + " for team " + teamName);
        if(players.getFirst().getID() == id){
            return players.getFirst();
        }else if (players.getLast().getID() == id){
            return players.getLast();
        }
        else{
            log.error("getPlayer() Player with ID " + id + " not found in team " + teamName);
            throw new IllegalArgumentException("Player not Found");
        }
    }

    public Player getCaptain() {
        log.info("Getting captain for team " + teamName + ": " + captain);
        return captain;
    }


    // --- SETTERS ---
    public void setDoublesName(String teamName) {
        log.info("Setting team name from " + this.teamName + " to " + teamName);
        this.teamName = Objects.requireNonNull(teamName, "Team name cannot be null");
    }

    public void setHomePhoneNumber(int homeNumber){
        log.info("Setting home phone number for team " + teamName + " to " + homeNumber);
        this.contactDetails.setHomePhoneNumber(homeNumber);
    }


    // --- STATS ---
    public BaseStats getOrCreateTeamStats(BaseStats_Key K) {
        log.info("Getting or creating team stats for team " + teamName + " with key: " + K);
        return this.stats.computeIfAbsent(K, _ -> new BaseStats());
    }


    // --- UPDATE ---
    public void updateHomeLocation(int homeNumber, String address){
        log.info("Updating home location for team " + teamName + " to " + address);
        this.contactDetails.updateHomeLocation(homeNumber, address);
    }


    // --- PLAYER MANAGEMENT ---
    public void addPlayer(Player p) {
        if (p == null) {
            log.error("addPlayer() Player cannot be null");
        }else if(players.size() >= 2){
            log.error("addPlayer() Team already has 2 players");
        }else if (p.isBye()) {
            log.error("addPlayer() Cannot add Bye player to team");
        }else if (players.contains(p)) {
            log.error("addPlayer() Player already in team");
        }else{
            players.add(p);
            if (captain == null) {
                updateCaptain(p);
            }
            p.getOrCreateStats( new BaseStats_Key(GLOBAL, super.getID()));
        }
        log.info("Added player " + p.getName() + " to team " + teamName);
    }

    public void removePlayer(int id) {
        log.info("Removing player with ID " + id + " from team " + teamName);
        Player toRemove = players.stream()
                .filter(p -> p.getID() == id)
                .findFirst()
                .orElseThrow(() -> {
                    log.error("removePlayer() Player with ID " + id + " not found in team " + teamName);
                    throw new IllegalArgumentException("Player not Found");
                });
        players.remove(toRemove);
        if (toRemove.equals(captain)) {
            toRemove.removeCaptain();
            captain = null;
            if (!players.isEmpty()) {
                updateCaptain(players.getFirst());
            }
        }
        log.info("Removed player " + toRemove.getName() + " from team " + teamName);
    }

    public void updateCaptain(Player newCaptain) {
        log.info("Updating captain for team " + teamName + " to " + newCaptain.getName());
        if (newCaptain == null) {
            log.info("updateCaptain() Captain cannot be null");
        }else if (newCaptain.isBye()) {
            log.info("updateCaptain() Bye player cannot be captain");
        }else if (!players.contains(newCaptain)) {
            log.info("updateCaptain() Captain must be in the team");
        }else{
            captain = newCaptain;
            captain.makeCaptain();
        }
        log.info("Updated captain for team " + teamName + " to " + newCaptain.getName());
    }
}
