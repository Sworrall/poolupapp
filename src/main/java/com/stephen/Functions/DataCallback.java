package com.stephen.Functions;

import com.stephen.Player.Player;

public interface DataCallback<T> {
    void onSuccess(Player firebasePlayer);

    void onError(Exception e);
}