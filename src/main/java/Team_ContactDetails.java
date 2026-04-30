public class Team_ContactDetails{
    private int locationPhoneNumber;
    private String address;

    private static final Logger log = LoggerFactory.getLogger(Functions.class);


    // --- CONSTRUCTOR ---
    Team_ContactDetails(String location){
        this.locationPhoneNumber = 0;
        this.address = location;
        log.info("Team_ContactDetails constructor called with location: " + location);
    }
    
    Team_ContactDetails(){
        this.locationPhoneNumber = 0;
        this.address = null;
        log.info("Team_ContactDetails constructor called with null location");
    }


    // --- GETTERS ---
    public int getLocationNumber(){
        return locationPhoneNumber;
        log.info("getLocationNumber called, returning: " + locationPhoneNumber);
    }

    public String getAddress(){
        return this.address;
        log.info("getAddress called, returning: " + address);
    }


    // --- SETTERS ---
    public void setLocationNumber(int locationNumber){
        this.locationPhoneNumber = locationNumber;
        log.info("setLocationNumber called with: " + locationNumber);
    }


    // --- UPDATE ---
    public void updateHomeLocation(int locationNumber, String address){
        this.locationPhoneNumber = locationNumber;
        this.address = address;
        log.info("updateHomeLocation called with: " + locationNumber + ", " + address);
    }

    public void updateHomeAddress(String homeAddress){
        this.address = homeAddress;
        log.info("updateHomeAddress called with: " + homeAddress);
    }

    public void setHomePhoneNumber(int homeNumber){
        this.locationPhoneNumber = homeNumber;
        log.info("setHomePhoneNumber called with: " + homeNumber);
    }
}
