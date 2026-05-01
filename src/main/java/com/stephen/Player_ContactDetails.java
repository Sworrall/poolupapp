package com.stephen;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Player_ContactDetails{
    private int phoneNumber;
    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTOR ---
    public Player_ContactDetails(int mobileNumber){
        this.phoneNumber = mobileNumber;
        log.info("Player_ContactDetails object created with phone number: {}", mobileNumber);
    }

    public Player_ContactDetails(){
        this.phoneNumber = 0;
        log.info("Player_ContactDetails object created with default phone number: {}", this.phoneNumber);
    }


    // --- GETTERS ---
    public int getPhoneNumber(){
        log.info("Retrieving phone number: {}", this.phoneNumber);
        return this.phoneNumber;
    }


    // --- SETTERS ---
    public void setPhoneNumber(int newNumber){
        this.phoneNumber = newNumber;
        log.info("Phone number updated to: {}", newNumber);
    }
}
