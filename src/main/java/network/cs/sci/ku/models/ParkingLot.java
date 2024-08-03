package network.cs.sci.ku.models;

import java.io.Serializable;
import java.util.*;

public class ParkingLot implements Serializable {
    private Map<String, String> parkedCars;

    public ParkingLot() {
        parkedCars = new HashMap<>();
    }

    public void addLicensePlate(String section, String licensePlate) {
        parkedCars.put(section, licensePlate);
    }

    public void removeLicensePlate(String licensePlate) {
        for (String s : parkedCars.keySet()) {
            String area = parkedCars.get(s);
            if (area != null && area.equalsIgnoreCase(licensePlate)) {
                addLicensePlate(s, null);
                return;
            }
        }
    }

    public ArrayList<String> getParkedAreas() {
        ArrayList<String> parkedAreas = new ArrayList<>();
        for (String area : parkedCars.keySet()) {
            if (parkedCars.get(area) != null) {
                parkedAreas.add(area);
            }
        }
        return parkedAreas;
    }

    public String getParked(String licensePlate) {
        for (String s : parkedCars.keySet()) {
            String area = parkedCars.get(s);
            if (area != null && area.equalsIgnoreCase(licensePlate)) {
                return s;
            }
        }
        return null;
    }

    public Map<String, String> getParkedCars() {
        return parkedCars;
    }
}
