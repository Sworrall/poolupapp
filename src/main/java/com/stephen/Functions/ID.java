package com.stephen.Functions;

import java.util.concurrent.atomic.AtomicInteger;


public class ID {
    private final int ID;
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);


    // --- CONSTRUCTORS ---
    public ID() {
        this.ID = NEXT_ID.getAndIncrement();
    }

    public int getID(){
        return this.ID;
    }
}
