package network.cs.sci.ku.services;

import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerProtocol extends ParkingProtocol{
    protected String commandRegex = "[0-4]";

    @Override
    public String decode(String text) throws IOException {
        String decoded = new String(Base64.getDecoder().decode(text));
        String contextRegex = "[^~]*";
        String regexes = commandRegex + splitRegex +contextRegex;
        Pattern pattern = Pattern.compile(regexes);
        Matcher matcher = pattern.matcher(decoded);
        if (!matcher.find()) {
            throw new IOException("Error to decode messages");
        }
        return decoded;
    }
}
