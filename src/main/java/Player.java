import java.util.*;

public class Player extends ID implements StatHolder<Player> {
    private static final int GLOBAL = 0;
    private String firstName;
    private String lastName;
    private String nickName;
    private final Player_ContactDetails contactDetails;
    private final boolean isBye;
    private boolean isCaptain;
    private final Map<BaseStats_Key, BaseStats> playerStatsMap;

    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTORS ---
    public Player(String firstName, String lastName) {
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        this.nickName = null;
        this.contactDetails = new Player_ContactDetails();
        this.isBye = false;
        this.isCaptain = false;
        this.playerStatsMap = new HashMap<>();
        this.playerStatsMap.put(new BaseStats_Key(GLOBAL, null), new BaseStats());
        info.log("Created player: " + getFullName());
    }

    Player() {
        super();
        this.firstName = "BYE";
        this.lastName = "";
        this.nickName = null;
        this.contactDetails = new Player_ContactDetails();
        this.isBye = true;
        this.isCaptain = false;
        this.playerStatsMap = new HashMap<>();
        info.log("Created bye player");
    }


    // --- FACTORY ---
    public static Player createBye(){
        info.log("Creating bye player");
        return new Player();
    }

    public Player createByeParty(){
        info.log("Creating bye party");
        return new Player();
    }



    // --- INTERFACE ---
    @Override
    public String getName() {
        info.log("Getting name for player: " + getFullName());
        return getFullName();
    }

    @Override
    public boolean isBye(){
        info.log("Checking if player is bye: " + getFullName() + " - " + isBye);
        return isBye;
    }

    @Override
    public BaseStats getOrCreateStats(BaseStats_Key K) {
        info.log("Getting or creating stats for player: " + getFullName() + " - Key: " + K);
        return playerStatsMap.computeIfAbsent(K, _ -> new BaseStats());
    }


    // --- GETTERS ---
    public String getFirstName() {
        info.log("Getting first name for player: " + getFullName());
        return firstName;
    }

    public String getLastName() {
        info.log("Getting last name for player: " + getFullName());
        return lastName;
    }

    public String getNickName() {
        info.log("Getting nickname for player: " + getFullName());
        return (nickName == null || nickName.isBlank()) ? "" : nickName;
    }

    public boolean isCaptain() {
        info.log("Checking if player is captain: " + getFullName() + " - " + isCaptain);
        return isCaptain;
    }

    public String getFullName() {
        if (isBye) return "BYE";
        String name = (nickName != null && !nickName.isBlank())
                ? firstName + " \"" + nickName + "\" " + lastName
                : firstName + " " + lastName;
        info.log("Getting full name for player: " + getFullName());
        return (isCaptain ? "(C) " : "") + name;
    }

    public Player_ContactDetails getContactDetails(){
        info.log("Getting contact details for player: " + getFullName());
        return this.contactDetails;
    }


    // --- SETTERS ---
    public void setName(String firstName, String lastName, String nickName) {
        this.firstName = Objects.requireNonNull(firstName);
        this.lastName = Objects.requireNonNull(lastName);
        updateNickName(nickName);
        info.log("Set name for player: " + getFullName());
    }

    public void makeCaptain() {
        this.isCaptain = true;
        info.log("Made player captain: " + getFullName());
    }

    public void removeCaptain() {
        this.isCaptain = false;
        info.log("Removed captain status from player: " + getFullName());
    }

    public void updateNickName(String nickName) {
        this.nickName = (nickName == null || nickName.isBlank()) ? null : nickName;
        info.log("Updated nickname for player: " + getFullName() + " - New Nickname: " + this.nickName);
    }

    public void setMobileNumber(int number) {
        contactDetails.setPhoneNumber(number);
        info.log("Set mobile number for player: " + getFullName() + " - New Mobile Number: " + number);
    }
}