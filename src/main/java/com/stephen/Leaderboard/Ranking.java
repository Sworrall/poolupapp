package com.stephen.Leaderboard;

import java.util.ArrayList;
import com.stephen.BaseStats.StatField;
import com.stephen.BaseStats.StatHolder;


public interface Ranking<S extends StatHolder<S>> {
    ArrayList<S> rank(ArrayList<S> parties, int eventID, StatField field);
}