package com.stephen;

public interface StatHolder<T> {

    int getID();

    String getName();

    boolean isBye();

    BaseStats getOrCreateStats(BaseStats_Key K);

    T createByeParty();
}
