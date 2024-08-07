package network.cs.sci.ku.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;

public abstract class ParkingProtocol {
    protected String splitRegex = "~";

    public abstract String decode(String text) throws IOException;

    public String encode(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    public void sentMessage(PrintWriter writer, String message) {
        String encoded = encode(message);
        writer.println(encoded);
    }

    public String receiveMessage(BufferedReader reader) throws IOException {
        String response = reader.readLine();
        if (response != null) {
            return decode(response);
        }
        return null;
    }

    public String createMessage(String cos, String context) {
        return cos + "~" + context;
    }

    public String createMessage(String cos) {
        return createMessage(cos, "none");
    }}
