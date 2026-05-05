package com.stephen.Player;


public class Player_ContactDetails{
    private int phoneNumber;


    // --- CONSTRUCTOR ---
    public Player_ContactDetails(int mobileNumber){
        this.phoneNumber = mobileNumber;
    }

    public Player_ContactDetails(){
        this.phoneNumber = 0;
    }


    // --- GETTERS ---
    public int getPhoneNumber(){
        return this.phoneNumber;
    }


    // --- SETTERS ---
    public void setPhoneNumber(int newNumber){
        this.phoneNumber = newNumber;
    }
}
