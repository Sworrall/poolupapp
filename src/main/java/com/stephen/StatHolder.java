package com.stephen;

import java.util.Map;


public interface StatHolder<S> {

    int getID();

    Map<BaseStats_Key, BaseStats> getStatsMap();

    String getName();

    boolean isBye();

    BaseStats getOrCreateStats(BaseStats_Key K);

    S createByeParty();
}