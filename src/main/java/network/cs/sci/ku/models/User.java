package network.cs.sci.ku.models;

import java.io.Serializable;

public class User implements Serializable {
    private String parkingID;
    private String licensePlate;

    public User() {}

    public User(String parkingID, String licensePlate) {
        this.parkingID = parkingID;
        this.licensePlate = licensePlate;
    }

    public boolean isID(String id) {
        return this.parkingID.equals(id);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getParkingID() {
        return parkingID;
    }
}
