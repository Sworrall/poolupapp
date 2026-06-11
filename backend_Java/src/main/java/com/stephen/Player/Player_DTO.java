package com.stephen.Player;

public class Player_DTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String nickName;
    private String fullName;
    private boolean captain;
    private boolean bye;
    private String firebaseUid;
    private String phoneNumber;

    public Player_DTO(Player_Entity entity) {
        this.id = entity.getId();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.nickName = entity.getNickName();
        this.fullName = entity.getName();
        this.captain = entity.isCaptain();
        this.bye = entity.isBye();
        this.firebaseUid = entity.getFirebaseUid();
        this.phoneNumber = entity.getPhoneNumber();
    }

    // getters only (important for FlutterFlow stability)
    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getNickName() { return nickName; }
    public String getFullName() { return fullName; }
    public boolean isCaptain() { return captain; }
    public boolean isBye() { return bye; }
    public String getFirebaseUid() { return firebaseUid; }
    public String getPhoneNumber() { return phoneNumber; }
}