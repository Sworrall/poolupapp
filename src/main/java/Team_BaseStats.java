import java.util.HashMap;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Team_BaseStats < S extends StatHolder<S>> {
    private int GLOBAL = 0;
    private final int teamID;
    private Map<BaseStats_Key, BaseStats> stats;

    private static final Logger log = LoggerFactory.getLogger(Team_BaseStats.class);


    // CONSTRUCTOR
    public Team_BaseStats(int id){
        this.teamID = id;
        this.stats = new HashMap<>();
        getOrCreateTeamStats(new BaseStats_Key(GLOBAL, teamID));
        log.info("Created Team_BaseStats for teamID: {}", teamID);
    }


    // --- GETTERS ---
    public Integer getTeamID() {
        log.info("Getting teamID: {}", teamID);
        return teamID;
    }

    public BaseStats getOrCreateTeamStats(BaseStats_Key K) {
        log.info("Getting or creating team stats for key: {}", K);
        return stats.computeIfAbsent(K, _ -> new BaseStats());
    }

    public BaseStats getOrCreateStats(int eventID, Integer teamID) {
        BaseStats_Key K = new BaseStats_Key(eventID, teamID);
        log.info("Getting or creating stats for eventID: {}, teamID: {}", eventID, teamID);
        return getOrCreateTeamStats(K);
    }
}
