package network.cs.sci.ku;

import network.cs.sci.ku.controllers.ParkingController;
import network.cs.sci.ku.services.ParkingProtocol;
import network.cs.sci.ku.services.ProtocolPrinter;
import network.cs.sci.ku.services.ServerProtocol;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12347)) {
            System.out.println("Server started. Waiting for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected. IP: " + clientSocket.getInetAddress().getHostAddress());
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
        String serverIp;
        String clientIp;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.serverIp = socket.getLocalAddress().getHostAddress();
            this.clientIp = socket.getInetAddress().getHostAddress();
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // receiver
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true); // sender
            ) {
                parkingProtocol = new ServerProtocol();
                parkingController = new ParkingController();
                parkingController.init();

                String request;
                while ((request = parkingProtocol.receiveMessage(in)) != null) { // receive message(s) from client
                    ProtocolPrinter.print("CLIENT", "RECEIVE", request, clientIp, serverIp);
                    String response = handleRequest(request);
                    ProtocolPrinter.print("SERVER", "SENT", response, serverIp, clientIp);
                    parkingProtocol.sentMessage(out, response);
                }

                System.out.println("Client Disconnected. IP: " + clientIp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String handleRequest(String request) {
            String message = null;
            String[] component = request.split("~");
            String command = component[0];
            String context = component[1];
            try {
                switch (command) {
                    case "0":
                        parkingController.register(context);
                        message = parkingProtocol.createMessage("201");
                        break;
                    case "1":
                        parkingController.login(context);
                        message = parkingProtocol.createMessage("201");
                        break;
                    case "2":
                        message = parkingProtocol.createMessage("200", parkingController.getParkingInfo());
                        break;
                    case "3":
                        parkingController.parkCar(context);
                        message = parkingProtocol.createMessage("200", parkingController.getUserPosition());
                        break;
                    case "4":
                        message = parkingProtocol.createMessage("201", String.valueOf(parkingController.getParkingFee()));
                        break;
                    default:
                        message = parkingProtocol.createMessage("402");
                        break;
                }
            } catch (IOException e) {
                message = parkingProtocol.createMessage(e.getMessage());
            }
            return message;
        }
    }
}
