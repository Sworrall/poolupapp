package com.stephen;

public interface Match_Factory<T extends StatHolder<T>> {

    Match<T> createMatch(T p1, T p2, int frameCount);
    Match<T> createMatch(T p1, int frameCount);
    Match<T> createMatch();

}