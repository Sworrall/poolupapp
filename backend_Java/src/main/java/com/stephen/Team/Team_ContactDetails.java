package com.stephen.Team;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Team_ContactDetails {

    @Column(name = "home_phone_number")
    private Long phoneNumber;

    @Column(name = "address")
    private String address;

    protected Team_ContactDetails() {}

    public Team_ContactDetails(Long phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public Long getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(Long phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public void updateHomeLocation(String address, Long number){
        setAddress(address);
        setPhoneNumber(number);
    }
}