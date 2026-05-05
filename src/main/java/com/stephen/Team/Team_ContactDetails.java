package com.stephen.Team;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class Team_ContactDetails {
    private int locationPhoneNumber;
    private String address;
    private static final Logger log = LoggerFactory.getLogger(Team_ContactDetails.class);


    // --- CONSTRUCTOR ---
    public Team_ContactDetails(String location) {
        this.locationPhoneNumber = 0;
        this.address = location;
    }

    Team_ContactDetails() {
        this.locationPhoneNumber = 0;
        this.address = null;
    }


    // --- GETTERS ---
    public int getLocationNumber() {
        return locationPhoneNumber;
    }

    public String getAddress() {
        return this.address;
    }


    // --- UPDATE ---
    public void updateLocationNumber(int locationNumber) {
        this.locationPhoneNumber = locationNumber;
    }

    public void updateHomeAddress(String homeAddress) {
        this.address = homeAddress;
    }

    public void setHomePhoneNumber(int homeNumber) {
        this.locationPhoneNumber = homeNumber;
    }

    public void updateContactDetails(int locationNumber, String address) {
        this.locationPhoneNumber = locationNumber;
        this.address = address;
    }
}
