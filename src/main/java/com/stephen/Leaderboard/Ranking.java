package com.stephen.Leaderboard;

import com.stephen.Stats.StatHolder;

import java.util.ArrayList;

public interface Ranking<S extends StatHolder<S>> {
    ArrayList<S> rank(ArrayList<S> parties, int eventID);
}