package network.cs.sci.ku.services;

public class ProtocolPrinter {
    public static void print(int side, String response, int status, String context) {
        String host;
        if (side == 0) {
            host = "Server";
        }else {
            host = "Client";
        }

        System.out.println(host + " response: " + response);
        System.out.println("Status: "  + status);
        System.out.println("Context: " + context);
    }
}
