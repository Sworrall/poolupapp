import java.util.ArrayList;
import java.util.Collections;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Tournament_RoundRobin <S extends StatHolder<S>> extends Tournament<S>{
    private final int frameCount;
    private final ArrayList<Match<S>> fixtures;
    private final Match_Factory<S> matchFactory;
    private final Leaderboard<S> leaderboard;
    private final Ranking_Points<S> rankingStrategy;
    private static final Logger log = LoggerFactory.getLogger(Tournament_RoundRobin.class);


    // --- CONSTRUCTOR ---
    public Tournament_RoundRobin(ArrayList<S> partyList, int frameCount, Match_Factory<S> matchFactory) {
        super(partyList);
        this.frameCount = frameCount;
        this.fixtures = new ArrayList<>();
        this.matchFactory = matchFactory;
        this.rankingStrategy = new Ranking_Points<>();
        this.leaderboard = new Leaderboard<>(partyList, super.getID(), rankingStrategy);
        generateTeamList();
        generateFixturesRR();
        log.info("Round Robin Tournament created with {} parties and {} matches.", partyList.size(), super.matchList.size());
    }


    // --- FUNCTIONS ---
    public void generateTeamList() {
        if(super.partyList.size() % 2 == 1) partyList.add(partyList.getFirst().createByeParty());
        Collections.shuffle(partyList);
        log.info("Team list generated for Round Robin Tournament.");   
    }

    public void generateFixturesRR(){
        int size = super.partyList.size();
        for (int i = 0; i < size; i++) {
            for (int j = i+1; j < size; j++) {
                super.matchList.add(matchFactory.createMatch(super.partyList.get(i), super.partyList.get(j), frameCount));
                this.fixtures.add(matchList.getLast());
            }
        }
        Collections.shuffle(matchList);
        log.info("Fixtures generated for Round Robin Tournament. Total matches: {}", super.matchList.size());
    }

    public ArrayList<S> playAll() {
        ArrayList<S> winners = new ArrayList<>();
        for (Match<S> m : super.matchList) {
            if(!m.isPlayed()){
                m.playMatch();
                winners.add(m.getWinner());
            }
        }
        log.info("All matches played in Round Robin Tournament.");
        return winners;
    }

    public boolean playAllCheck() {
        for (Match<S> m : super.matchList) {
            if(!m.isPlayed()){
                log.warn("Not all matches have been played. allPlayCheck failed.");
                return false;
            }
        }
        log.info("All matches have been played in Round Robin Tournament.");
        return true;
    }

    public ArrayList<S> getPremote(int premoteAmount){
        if(playAllCheck()){
            ArrayList<S> Premoted = ((Ranking_Points<S>) leaderboard.getStrategy()).rank(getAllParties(), super.getID());
            return new ArrayList<>(Premoted.subList(0, premoteAmount));
        }else{
            log.warn("Not all matches have been played. getPremoted failed.");
            throw new IllegalStateException("Not all matches have been played. getPremoted failed.");
        }
    }
    
    public ArrayList<S> getDemote(int DemoteAmount){
        if(playAllCheck()){
            ArrayList<S> Demoted = ((Ranking_Points<S>) leaderboard.getStrategy()).rank(getAllParties(), super.getID());
            return new ArrayList<>(Demoted.subList(Demoted.size() - DemoteAmount, Demoted.size()));
        }else{
            log.warn("Not all matches have been played. getDemote failed.");
            throw new IllegalStateException("Not all matches have been played. getDemote failed.");
        }
    }
}
