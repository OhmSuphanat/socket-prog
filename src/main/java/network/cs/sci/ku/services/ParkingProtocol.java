package network.cs.sci.ku.services;

import java.util.Base64;

public class ParkingProtocol implements Encoder {

    @Override
    public String encode(int status, String context) {
        String base64 = Base64.getEncoder().encodeToString(context.getBytes());
        return status+base64;
    }

    @Override
    public String decode(String encoded) {
        int status = Integer.valueOf(encoded.substring(0, 3));
        String nonBase64 = new String(Base64.getDecoder().decode(encoded.substring(3)));
        return status + nonBase64;
    }

    public String getServerMessage(int status) {
        switch (status) {
            case 200:
                return "The request was successful, and the server returned the requested resource.";
            case 201:
                return "Your ParkingID is created.";
            case 202:
                return "Welcome back!!!";
            case 400:
                return "Bad Request, wrong input format.";
            case 401:
                return "Unknown ParkingID, please try again, or ParkingID was expired, pay parking ensures spot availability.";
            case 402:
                return "This section is unavailable to park.";
        }
        return null;
    }

    public String createMessage(int status) {
        return encode(status, RandomString.getRandomAlphabeticString(110));
    }

    public String createMessage(int status, String context) {
        return encode(status, context);
    }
}
