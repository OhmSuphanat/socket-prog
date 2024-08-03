package network.cs.sci.ku.services;

public class ProtocolPrinter {
    public static void print(String response, int status, String context) {
        System.out.println("Server response: " + response);
        System.out.println("Status: "  + status);
        System.out.println("Context: " + context);
    }
}
