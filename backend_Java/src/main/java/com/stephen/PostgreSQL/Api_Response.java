package com.stephen.PostgreSQL;

public class Api_Response<T> {

    private T data;

    public Api_Response(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}