package com.stephen.Functions;

public interface DataCallback<T> {
    void onSuccess(T result); // Called when data is ready
    void onError(Exception e); // Called if something goes wrong
}