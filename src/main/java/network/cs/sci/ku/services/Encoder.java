package network.cs.sci.ku.services;

public interface Encoder {
    String encode(int status, String context);
    String decode(String encoded);
}
