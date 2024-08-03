package network.cs.sci.ku.models;

public class Message {
    public static String getParking(ParkingLot parkingLot) {
        StringBuilder sb = new StringBuilder();
        sb.append("0 ");
        for (String s : parkingLot.getParkedCars().keySet()) {
            if (parkingLot.getParkedCars().get(s) == null) {
                sb.append(s+" ");
            }
        }
        return sb.toString();
    }
}
