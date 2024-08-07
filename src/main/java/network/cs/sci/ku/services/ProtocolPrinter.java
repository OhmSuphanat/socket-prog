package network.cs.sci.ku.services;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ProtocolPrinter {
    private static Map<String, String> phraseMap = new HashMap<>();

    static {
        phraseMap.put("200", "Request successful, provide resource back with context.");
        phraseMap.put("201", "Request successful, give nothing back.");
        phraseMap.put("400", "Failure Request, provided context wasn't found in database.");
        phraseMap.put("401", "Failure Request, can't not give request resource at the time.");
        phraseMap.put("402", "Failure Request, Unknown command");
        phraseMap.put("0", "Register with given context.");
        phraseMap.put("1", "Login with given context.");
        phraseMap.put("2", "Request unavailable parking spots and current parked spot.");
        phraseMap.put("3", "Park with given context.");
    }

    public static void print(String host, String verb, String text, String sip, String dip) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:n");
        String formattedDateTime = currentDateTime.format(formatter);

        String[] component = text.split("~");
        System.out.println("|---                         Application Protocol                                ---|");
        if (verb.equalsIgnoreCase("SENT")) {
            System.out.println("SENT MESSAGE.o0");
        } else {
            System.out.println("RECEIVE MESSAGE FROM " + host +" .o0");
        }
        System.out.println("Date: " + formattedDateTime);
        System.out.println("SOURCE IP: " + sip);
        System.out.println("DESTINATION IP: " + dip);

        if (host.equalsIgnoreCase("SERVER")) {
            System.out.println("STATUS: " + component[0]);
            System.out.println("STATUS PHRASE: " + getPhrase(component[0]));
        } else {
            System.out.println("COMMAND: " + component[0]);
            System.out.println("COMMAND PHRASE: " + getPhrase(component[0]));
        }
        System.out.println("CONTEXT:" + component[1]);
    }

    public static String getPhrase(String cos) {
        return phraseMap.get(cos);
    }

}
