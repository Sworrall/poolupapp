package com.stephen.Doubles;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Doubles_ContactDetails {

    @Column(name = "home_phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    protected Doubles_ContactDetails() {}

    public Doubles_ContactDetails(String phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // --- GETTERS & SETTERS ---
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public void updateHomeLocation(String address, String number) {
        setAddress(address);
        setPhoneNumber(number);
    }
}