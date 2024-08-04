package network.cs.sci.ku;

import network.cs.sci.ku.controllers.ParkingController;
import network.cs.sci.ku.services.ParkingProtocol;
import network.cs.sci.ku.services.ProtocolPrinter;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started. Waiting for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected.");
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        ParkingController parkingController;
        ParkingProtocol parkingProtocol;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // receiver
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // sender
            ) {
                parkingController = new ParkingController();
                parkingProtocol = new ParkingProtocol();
                parkingController.init();

                String request;
                while ((request = in.readLine()) != null) { // receive message(s) from client
                    String message = handleRequest(request);
                    System.out.println("Server sent message: " + message);
                    out.println(message); // send message(s) to client
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String handleRequest(String response) {
            String serverMessage = null;
            String message = null;
            response = parkingProtocol.decode(response);
            int clientStatus = Integer.parseInt(response.substring(0, 3));
            String context = response.substring(3);
            ProtocolPrinter.print(1, response, clientStatus, context);
            if (clientStatus == 800) {
                try {
                    parkingController.register(context);
                    message = parkingProtocol.createMessage(201);
                } catch (IOException e) {
                    message = parkingProtocol.createMessage(Integer.valueOf(e.getMessage()));
                }

            }else if (clientStatus == 801){
                try {
                    parkingController.login(context);
                    message = parkingProtocol.createMessage(202);
                } catch (IOException e) {
                    message = parkingProtocol.createMessage(Integer.valueOf(e.getMessage()));
                }
            } else if (clientStatus == 802) {
                message = parkingProtocol.createMessage(200, parkingController.getParkedAreas());
            } else if (clientStatus == 803) {
                message = parkingProtocol.createMessage(200, parkingController.getUserPosition());
            } else if (clientStatus == 804) {
                try {
                    parkingController.parkCar(context);
                    message = parkingProtocol.createMessage(200, parkingController.getUserPosition());
                } catch (IOException e) {
                    message = parkingProtocol.createMessage(Integer.valueOf(e.getMessage()), parkingController.getUserPosition());
                }
            } else if (clientStatus == 805) {
                double fee = parkingController.getParkingFee();
                message = parkingProtocol.createMessage(200, String.valueOf(fee));
            }
            return message;
        }
    }
}
