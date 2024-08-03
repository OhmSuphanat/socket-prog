package network.cs.sci.ku;


import network.cs.sci.ku.controllers.ParkingController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        LocalDateTime time = LocalDateTime.parse("20240804020714", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.out.println(Duration.between(time, LocalDateTime.now()).toMinutes());
    }
}
