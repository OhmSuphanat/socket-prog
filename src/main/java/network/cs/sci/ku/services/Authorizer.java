package network.cs.sci.ku.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Authorizer {
    public static String register() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return  localDateTime.format(dateTimeFormatter);
    }
}
