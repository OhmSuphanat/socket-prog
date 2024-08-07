package network.cs.sci.ku;

import network.cs.sci.ku.services.*;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
    private static LocalDateTime endTime;
    private static ScheduledExecutorService scheduler;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12347)) {
            String clientIp = socket.getLocalAddress().getHostAddress();
            String serverIp = socket.getInetAddress().getHostAddress();
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //sender
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //receiver
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            ParkingProtocol parkingProtocol = new ClientProtocol();
            String userInput;
            String serverStatus;
            String request = null;
            String response;
            String userID;
            endTime = LocalDateTime.now().plusSeconds(30);
            startCountdown(socket);

            WelcomePrinter.print();
            System.out.println();

            while (true) {
                System.out.println("|---                          Authorization...                               ---|");
                AuthorizedPrinter.print();
                userInput = stdIn.readLine();
                extendTime(60);
                System.out.println();

                if (userInput.equalsIgnoreCase("register") || userInput.equalsIgnoreCase("r")) {
                    userID = Authorizer.register().strip();
                    System.out.print("Type your licensePlate (with no space): ");
                    String licensePlate = stdIn.readLine().strip();
                    if (!licensePlate.contains(" ")) {
                        request = userID + " " + licensePlate;
                        request = parkingProtocol.createMessage("0", request);
                    }
                }else if (userInput.equalsIgnoreCase("login") || userInput.equalsIgnoreCase("l")){
                    System.out.print("Type your ParkingID (with no space): ");
                    userID = stdIn.readLine();
                    if (!userID.contains(" ")) {
                        request = parkingProtocol.createMessage("1", userID);
                    }
                } else {
                    continue;
                }

                ProtocolPrinter.print("Client", "SENT", request, clientIp, serverIp);
                parkingProtocol.sentMessage(out, request);

                response = parkingProtocol.receiveMessage(in);
                ProtocolPrinter.print("SERVER", "RECEIVE", response, serverIp, clientIp);
                String[] components = response.split("~");
                serverStatus = components[0];

                if (serverStatus.equalsIgnoreCase("201")) {
                    break;
                }
            }

            System.out.println("|---                         Loading ParkingLot...                               ---|");
            request = parkingProtocol.createMessage("2");
            ProtocolPrinter.print("Client", "SENT", request, clientIp, serverIp);
            parkingProtocol.sentMessage(out, request);

            response = parkingProtocol.receiveMessage(in);
            ProtocolPrinter.print("SERVER", "RECEIVE", response, serverIp, clientIp);
            String[] components = response.split("~");
            String[] parkingInfo = components[1].split(" ");
            String parkedCoords = parkingInfo[0];
            String userPosition = parkingInfo[1];

            while (true) {
                extendTime(30);
                System.out.println("|---                              ParkingLot                                     ---|");
                System.out.println("Your ParkingID is " + "'" + userID + "'.");
                ParkingLotPrinter.print(userPosition, parkedCoords);
                if (userPosition.equalsIgnoreCase("none")) {
                    System.out.print("Type a available position such as 'C4': ");
                    String position = stdIn.readLine();
                    String regex = "^[A-z][1-9]$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(position);
                    if (matcher.find()) {
                        request = parkingProtocol.createMessage("3", position);
                        ProtocolPrinter.print("Client", "SENT", request, clientIp, serverIp);
                        parkingProtocol.sentMessage(out, request);

                        response = parkingProtocol.receiveMessage(in);
                        ProtocolPrinter.print("SERVER", "RECEIVE", response, serverIp, clientIp);
                        components = response.split("~");
                        userPosition = components[1];
                    }
                    continue;

                }
                break;
            }
            System.out.print("Type 'fee[f]' to check Parking fee: ");
            while ((userInput=stdIn.readLine()) != null) {
                if (userInput.equalsIgnoreCase("fee") || userInput.equalsIgnoreCase("f")) {
                    extendTime(30);
                    request = parkingProtocol.createMessage("4");
                    ProtocolPrinter.print("Client", "SENT", request, clientIp, serverIp);
                    parkingProtocol.sentMessage(out, request);

                    response = parkingProtocol.receiveMessage(in);
                    ProtocolPrinter.print("SERVER", "RECEIVE", response, serverIp, clientIp);
                    components = response.split("~");
                    double fee = Double.valueOf(components[1]);
                    System.out.println("Parking fee: " + fee + " Baht.");
                }
                System.out.print("Type 'fee[f]' to check Parking fee: ");
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: localhost");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: localhost");
            System.exit(1);
        }
    }

    public static void startCountdown(Socket socket) {
        scheduler = Executors.newScheduledThreadPool(1);

        Runnable countdownTask = new Runnable() {
            @Override
            public void run() {
                Duration remaining = Duration.between(LocalDateTime.now(), endTime);
                if (remaining.isNegative() || remaining.isZero()) {
                    System.out.println("Timeout!");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Runtime.getRuntime().exit(0);
                    scheduler.shutdown(); // Shutdown the scheduler when countdown is finished
                }
            }
        };

        // Schedule the task to run every second
        scheduler.scheduleAtFixedRate(countdownTask, 0, 1, TimeUnit.SECONDS);
    }

    public static void extendTime(int seconds) {
        endTime = LocalDateTime.now().plusSeconds(seconds);
    }
}


