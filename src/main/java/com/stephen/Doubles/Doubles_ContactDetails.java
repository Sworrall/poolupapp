package com.stephen.Doubles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Doubles_ContactDetails {
    private int locationPhoneNumber;
    private String address;
    private static final Logger log = LoggerFactory.getLogger(com.stephen.Team.Team_ContactDetails.class);


    // --- CONSTRUCTOR ---
    public Doubles_ContactDetails(String location){
        this.locationPhoneNumber = 0;
        this.address = location;
        log.info("Team_ContactDetails constructor called with location: {}", location);
    }

    public Doubles_ContactDetails(){
        this.locationPhoneNumber = 0;
        this.address = null;
        log.info("Team_ContactDetails constructor called with null location");
    }


    // --- GETTERS ---
    public int getLocationNumber(){
        log.info("getLocationNumber called, returning: {}", locationPhoneNumber);
        return locationPhoneNumber;
    }

    public String getAddress(){
        log.info("getAddress called, returning: {}", address);
        return this.address;
    }


    // --- SETTERS ---
    public void setLocationNumber(int locationNumber){
        this.locationPhoneNumber = locationNumber;
        log.info("setLocationNumber called with: {}", locationNumber);
    }


    // --- UPDATE ---
    public void updateHomeLocation(int locationNumber, String address){
        this.locationPhoneNumber = locationNumber;
        this.address = address;
        log.info("updateHomeLocation called with: {}, {}", locationNumber, address);
    }

    public void updateHomeAddress(String homeAddress){
        this.address = homeAddress;
        log.info("updateHomeAddress called with: {}", homeAddress);
    }

    public void setHomePhoneNumber(int homeNumber){
        this.locationPhoneNumber = homeNumber;
        log.info("setHomePhoneNumber called with: {}", homeNumber);
    }
}
