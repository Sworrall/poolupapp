import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class FrameFactory_Team implements FrameFactory<Team> {
    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTORS ---
    public Frame<Team> createFrame(Team teamA, Team teamB, Player playerA, Player playerB){
        log.info("Creating frame with teams: {} vs {}, players: {} vs {}", teamA.getName(), teamB.getName(), playerA.getName(), playerB.getName());
        return new Frame_Team<>(teamA, teamB, playerA, playerB);
    }

    public Frame<Team> createFrame(Team team, Player player){
        log.info("Creating frame with team: {}, player: {}", team.getName(), player.getName());
        return new Frame_Team<>(team, player);
    }


    // --- FACTORY METHODS ---
    @Override
    public Frame<Team> createFrame(Team t1, Team t2) {
        Player pA = UserInput.pickPlayer(t1.getPlayers());
        Player pB = UserInput.pickPlayer(t2.getPlayers());
        log.info("Creating frame with teams: {} vs {}, players: {} vs {}", t1.getName(), t2.getName(), pA.getName(), pB.getName());
        return new Frame_Team<>(t1, t2, pA, pB);
    }

    @Override
    public Frame<Team> createFrame(Team t1) {
        Player pA = UserInput.pickPlayer(t1.getPlayers());
        log.info("Creating frame with team: {}, player: {}", t1.getName(), pA.getName());
        return new Frame_Team<>(t1, pA);
    }

    @Override
    public Frame<Team> createFrame() {
        log.info("Creating empty frame for teams");
        return new Frame_Team<>();
    }
}