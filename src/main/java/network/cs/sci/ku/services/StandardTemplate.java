package network.cs.sci.ku.services;


import network.cs.sci.ku.models.ParkingLot;

public class StandardTemplate implements ParkingTemplate<ParkingLot>{
    @Override
    public ParkingLot build() {
        ParkingLot parkingLot = new ParkingLot();
        for (int i=0; i<7; i++) {
            for (int j=0; j<5; j++) {
                parkingLot.addLicensePlate(String.valueOf((char)(65 + i)) + (j+1), null );
            }
        }
        return  parkingLot;
    }
}
