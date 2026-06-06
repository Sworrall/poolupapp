package com.stephen.Player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Player_ContactDetails {

    @Column(name = "phone_number")
    private String phoneNumber;

    protected Player_ContactDetails() {}

    public Player_ContactDetails(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}