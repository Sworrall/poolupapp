package com.stephen.Doubles;

public class Doubles_Request {

    private Long player1ID;
    private Long player2ID;
    private String teamName;
    private String firebaseUID;
    private String phoneNumber;
    private String address;

    public Doubles_Request() {}

    // --- GETTERS & SETTERS ---
    public Long getPlayer1ID() { return player1ID; }
    public void setPlayer1ID(Long player1ID) { this.player1ID = player1ID; }

    public Long getPlayer2ID() { return player2ID; }
    public void setPlayer2ID(Long player2ID) { this.player2ID = player2ID; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getFirebaseUID() { return firebaseUID; }
    public void setFirebaseUID(String firebaseUID) { this.firebaseUID = firebaseUID; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
