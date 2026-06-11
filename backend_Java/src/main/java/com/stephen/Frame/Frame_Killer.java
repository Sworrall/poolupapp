package com.stephen.Frame;


import jakarta.persistence.*;

@Entity
@Table(name = "frames_killer")
@DiscriminatorValue("KILLER")
public class Frame_Killer extends Frame_Entity {

    @Column(name = "starting_lives", nullable = false)
    private int startingLives;

    protected Frame_Killer() {}

    public Frame_Killer(int startingLives) {
        this.startingLives = startingLives;
    }

    public int getStartingLives() { return startingLives; }
}