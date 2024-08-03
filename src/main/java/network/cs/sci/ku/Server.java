package network.cs.sci.ku;

import network.cs.sci.ku.controllers.ParkingController;
import network.cs.sci.ku.services.ParkingProtocol;

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
                    System.out.println("Received: " + request);
                    String response = handleRequest(request);
                    out.println(response); // send message(s) to client
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String handleRequest(String request) {
            String serverMessage = null;
            String message = null;
            request = parkingProtocol.decode(request);
            int clientStatus = Integer.parseInt(request.substring(0, 3));
            String context = request.substring(3);
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
            } else if (clientStatus == 504) {

            }
            return message;
        }
    }
}
