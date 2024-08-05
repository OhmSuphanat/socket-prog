package network.cs.sci.ku;

import network.cs.sci.ku.models.ParkingLot;
import network.cs.sci.ku.services.*;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Client {
    private static LocalDateTime endTime;
    private static ScheduledExecutorService scheduler;

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true); //sender
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //receiver
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            ParkingProtocol parkingProtocol = new ParkingProtocol();
            ParkingTemplate<ParkingLot> parkingTemplate = new StandardTemplate();
            ParkingLot parkingLot = parkingTemplate.build();
            String userInput;
            int serverStatus;
            String context;
            String message = null;
            String response;
            String userID;
            endTime = LocalDateTime.now().plusSeconds(30);
            startCountdown();

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
                    message = userID + " " + licensePlate;
                    message = parkingProtocol.encode(800, message);
                }else if (userInput.equalsIgnoreCase("login") || userInput.equalsIgnoreCase("l")){
                    System.out.print("Type your ParkingID (with no space): ");
                    userID = stdIn.readLine();
                    message = parkingProtocol.encode(801, userID);
                } else {
                    continue;
                }

                System.out.println("Client sent message: " + message);
                out.println(message);

                response = in.readLine();
                response = parkingProtocol.decode(response);
                serverStatus = Integer.parseInt(response.substring(0, 3));
                context = parkingProtocol.getServerMessage(serverStatus);

                ProtocolPrinter.print(0, response, serverStatus, context);
                if (serverStatus >= 200 && serverStatus < 300) {
                    break;
                }
            }
            System.out.println();

            System.out.println("|---                         Loading ParkingLot...                               ---|");
            message = parkingProtocol.createMessage(802);
            System.out.println("Client sent message: " + message);
            out.println(message);

            response = in.readLine();
            response = parkingProtocol.decode(response);
            serverStatus = Integer.parseInt(response.substring(0, 3));
            context = parkingProtocol.getServerMessage(serverStatus);
            String parkedCoords = response.substring(3);
            ProtocolPrinter.print(0, response, serverStatus, context);

            message = parkingProtocol.createMessage(803);
            System.out.println("Client sent message: " + message);
            out.println(message);

            response = in.readLine();
            response = parkingProtocol.decode(response);
            serverStatus = Integer.parseInt(response.substring(0, 3));
            context = parkingProtocol.getServerMessage(serverStatus);
            String userPosition = response.substring(3);
            ProtocolPrinter.print(0, response, serverStatus, context);
            System.out.println();

            while (true) {
                extendTime(30);
                System.out.println("|---                              ParkingLot                                     ---|");
                System.out.println("Your ParkingID is " + "'" + userID + "'.");
                ParkingLotPrinter.print(userPosition, parkedCoords);
                System.out.println();
                if (userPosition.equalsIgnoreCase("none")) {
                    System.out.print("Type the available position such as 'C4': ");
                    String position = stdIn.readLine();
                    message = parkingProtocol.createMessage(804, position);
                    System.out.println("Client sent message: " + message);
                    out.println(message);

                    response = in.readLine();
                    response = parkingProtocol.decode(response);
                    serverStatus = Integer.parseInt(response.substring(0, 3));
                    context = parkingProtocol.getServerMessage(serverStatus);
                    userPosition = response.substring(3);
                    ProtocolPrinter.print(0, response, serverStatus, context);
                    System.out.println();
                    continue;
                }
                break;
            }
            System.out.print("Type 'fee[f]' to check Parking fee: ");
            while ((userInput=stdIn.readLine()) != null) {
                if (userInput.equalsIgnoreCase("fee") || userInput.equalsIgnoreCase("f")) {
                    extendTime(30);
                    message = parkingProtocol.createMessage(805);
                    System.out.println("Client sent message: " + message);
                    out.println(message);

                    response = in.readLine();
                    response = parkingProtocol.decode(response);
                    serverStatus = Integer.parseInt(response.substring(0, 3));
                    context = parkingProtocol.getServerMessage(serverStatus);
                    ProtocolPrinter.print(0, response, serverStatus, context);
                    double fee = Double.valueOf(response.substring(3));
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

    public static void startCountdown() {
        scheduler = Executors.newScheduledThreadPool(1);

        Runnable countdownTask = new Runnable() {
            @Override
            public void run() {
                Duration remaining = Duration.between(LocalDateTime.now(), endTime);
                if (remaining.isNegative() || remaining.isZero()) {
                    System.out.println("Timeout!");
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


