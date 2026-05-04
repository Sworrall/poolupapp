package com.stephen.BaseStats;

import java.util.Map;


public interface StatHolder<S> {

    int getID();

    Map<BaseStats_Key, BaseStats> getStatsMap();

    String getName();

    boolean isBye();

    BaseStats getOrCreateStats(BaseStats_Key K);

    S createByeParty();

    void updateCloud_StatHolder();

    void updateCloud_Stats();

    default void updateCloud_All(){
        updateCloud_StatHolder();
        updateCloud_Stats();
    }
}