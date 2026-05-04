package com.stephen.MatchFactory;

import com.stephen.Match.Match;
import com.stephen.BaseStats.StatHolder;

public interface Match_Factory<S extends StatHolder<S>> {
    Match<S> createMatch(S p1, S p2, int frameCount);
    Match<S> createMatch(S p1, int frameCount);
    Match<S> createMatch();
}