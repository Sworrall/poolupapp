package com.stephen.Frame.Killer;


import com.stephen.Frame.Frame;
import jakarta.persistence.*;

@Entity
@Table(name = "frames_killer")
@DiscriminatorValue("KILLER")
public class Frame_Killer extends Frame {

    @Column(name = "starting_lives", nullable = false)
    private int startingLives;

    protected Frame_Killer() {}

    public Frame_Killer(int startingLives) {
        this.startingLives = startingLives;
    }

    public int getStartingLives() { return startingLives; }
}