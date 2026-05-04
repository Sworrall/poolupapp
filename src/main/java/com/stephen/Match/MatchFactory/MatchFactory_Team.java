package com.stephen.Match.MatchFactory;

import com.stephen.Frame.FrameFactory.FrameFactory;
import com.stephen.Frame.FrameFactory.FrameFactory_Team;
import com.stephen.Match.Match;
import com.stephen.Match.Match_Team;
import com.stephen.Team.Team;

public class MatchFactory_Team implements Match_Factory<Team> {

    private final FrameFactory<Team> frameFactory;

    public MatchFactory_Team() {
        this.frameFactory = new FrameFactory_Team();
    }

    public MatchFactory_Team(FrameFactory<Team> frameFactory) {
        this.frameFactory = frameFactory;
    }

    @Override
    public Match<Team> createMatch(Team p1, Team p2, int frameCount) {return new Match_Team(p1, p2, frameCount, frameFactory);}

    @Override
    public Match<Team> createMatch(Team p1, int frameCount) {
        return new Match_Team(p1, frameCount, frameFactory);
    }

    @Override
    public Match<Team> createMatch() {
        return new Match_Team(frameFactory);
    }
}