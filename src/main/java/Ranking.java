import java.util.ArrayList;

public interface Ranking<S extends StatHolder<S>> {
    ArrayList<S> rank(ArrayList<S> parties, int eventID);
}