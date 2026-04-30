import java.util.ArrayList;
import java.util.Comparator;

public class Ranking_Points<S extends StatHolder<S>> implements RankStrategy<S> {

    private static final Logger log = LoggerFactory.getLogger(Functions.class);

    @Override
    public ArrayList<S> rank(ArrayList<S> parties, int eventID) {
        ArrayList<S> sorted = new ArrayList<>(parties);
        sorted.sort(Comparator
            .comparingInt((S s) -> getstat(s, eventID, StatField.MATCH_WIN))
            .thenComparingInt(s -> getstat(s, eventID, StatField.FRAME_WIN))
            .thenComparingInt(s -> getstat(s, eventID, StatField.FRAME_WIN) - getstat(s, eventID, StatField.FRAME_LOSS))
            .thenComparingInt(s -> getstat(s, eventID, StatField.FRAME_BREAK_DISH))
            .reversed()
        );
        log.info("Ranking_Points: Sorted parties based on points for eventID {}: {}", eventID, sorted);
        return sorted;
    }

    private int getstat(S s, int eventID, StatField field) {
        BaseStats_Key key = new BaseStats_Key(eventID, s.getID());
        log.info("Ranking_Points: Retrieving stat for party ID {}: eventID {}, field {}", s.getID(), eventID, field);
        return s.getOrCreateStats(key).get(field);
    }
}