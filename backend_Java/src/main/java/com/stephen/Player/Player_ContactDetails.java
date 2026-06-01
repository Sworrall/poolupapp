package com.stephen.Player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Player_ContactDetails {

    @Column(name = "phone_number")
    private Long phoneNumber;

    protected Player_ContactDetails() {}

    public Player_ContactDetails(Long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(Long phoneNumber) { this.phoneNumber = phoneNumber; }
}