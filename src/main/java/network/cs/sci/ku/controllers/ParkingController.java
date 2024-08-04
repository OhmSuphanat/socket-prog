package network.cs.sci.ku.controllers;

import network.cs.sci.ku.models.*;
import network.cs.sci.ku.services.*;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ParkingController {
    Datasource userListDatasource;
    Datasource parkingLotHashMapDatasource;
    UserList userList;
    ParkingLot parkingLot;
    User user;



    public void init() {
        userListDatasource = new UserListDatasource("data/json", "users.json");
        parkingLotHashMapDatasource = new ParkingLotHashMapDatasource("data/json", "parkingLot.json");
        userList = (UserList) userListDatasource.readData();
        parkingLot = (ParkingLot) parkingLotHashMapDatasource.readData();
        if (parkingLot.getParkedCars().isEmpty()) {
            ParkingTemplate parkingTemplate = new StandardTemplate();
            parkingLot = (ParkingLot) parkingTemplate.build();
        }
        maintain();
    }

    public void register(String doubleID) throws IOException {
        maintain();
        String[] ids = doubleID.split(" ");
        if (ids.length > 2) {
            throw new IOException("400");
        }
        String parkingID = ids[0];
        String licensePlate = ids[1];
        user = new User(parkingID, licensePlate);
        userList.addObject(user);
        autosave();
    }

    public void login(String parkingID) throws IOException {
        maintain();
        if (parkingID.contains(" ")) {
            throw new IOException("400");
        }

        user = userList.findObjectByPK(parkingID);
        if (user == null) {
            throw new IOException("401");
        }
    }

    private void autosave() {
        userListDatasource.writeData(userList);
        parkingLotHashMapDatasource.writeData(parkingLot);
        userList = (UserList) userListDatasource.readData();
        parkingLot = (ParkingLot) parkingLotHashMapDatasource.readData();
    }

    public String getParkedAreas() {
        ArrayList<String> parkedArea = parkingLot.getParkedAreas();
        StringBuilder sb = new StringBuilder();
        for (String s : parkedArea) {
            sb.append(s);
        }
        if (sb.isEmpty()) {
            sb.append("none");
        }
        return sb.toString();
    }

    public String getUserPosition() {
        String position = parkingLot.getParked(user.getLicensePlate());
        return position == null ? "none" : position;
    }

    public void parkCar(String position) throws IOException {
        position = position.strip().toUpperCase();
        if (!parkingLot.getParkedCars().containsKey(position)) {
            throw new IOException("400");
        }
        String exist = parkingLot.getParkedCars().get(position);
        if (exist != null) {
            throw new IOException("402");
        }
        parkingLot.addLicensePlate(position, user.getLicensePlate());
        autosave();
    }

    private void maintain() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        List<User> userListCopy = new ArrayList<>(userList.getUsers());

        for (User user1 : userListCopy) {
            LocalDateTime userLocalDateTime = LocalDateTime.parse(user1.getParkingID(), formatter);
            if (Duration.between(userLocalDateTime, LocalDateTime.now()).toMinutes() > 24) {
                parkingLot.removeLicensePlate(user1.getLicensePlate());
                userList.removeUser(user1); // Remove from the original list
            }
        }
        autosave();
    }

    public double getParkingFee() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime userLocalDateTime = LocalDateTime.parse(user.getParkingID(), formatter);
        double fee = 0;
        double duration = Duration.between(userLocalDateTime, LocalDateTime.now()).toMinutes();
        if (duration > 1) {
            fee += (duration-1) * 20;
        }
        return fee;
    }
}
