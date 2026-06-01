package com.stephen.Player;

public class Player_Request {
    private String firstName;
    private String lastName;
    private String nickName;
    private Long phoneNumber;
    private String firebaseUID;

    // getters & setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public Long getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(Long phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFirebaseUID() { return firebaseUID; }
    public void setFirebaseUID(String firebaseUID) { this.firebaseUID = firebaseUID; }
}