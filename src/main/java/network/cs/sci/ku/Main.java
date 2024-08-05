package network.cs.sci.ku;


import network.cs.sci.ku.controllers.ParkingController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        LocalDateTime time1 = LocalDateTime.parse("20240804020714", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        LocalDateTime time2 = LocalDateTime.parse("20240804020814", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.out.println(Duration.between(time1, time2).toMinutes());
    }
}
