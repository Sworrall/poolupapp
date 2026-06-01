package com.stephen.Team;

public class Team_Request {
    private String teamName;
    private Long phoneNumber;
    private String address;
    private String firebaseUID;

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Long getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(Long phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getFirebaseUID() { return firebaseUID; }
    public void setFirebaseUID(String firebaseUID) { this.firebaseUID = firebaseUID; }
}