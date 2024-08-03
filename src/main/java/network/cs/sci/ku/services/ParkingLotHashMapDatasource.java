package network.cs.sci.ku.services;

import network.cs.sci.ku.models.ParkingLot;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class ParkingLotHashMapDatasource implements Datasource<ParkingLot> {
    private String directoryName;
    private String fileName;
    private ObjectMapper objectMapper;

    public ParkingLotHashMapDatasource(String directoryName, String fileName) {
        this.directoryName = directoryName;
        this.fileName = fileName;
        this.objectMapper = new ObjectMapper();
        checkFileIsExisted();
    }

    private void checkFileIsExisted() {
        File file = new File(directoryName);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = directoryName + File.separator + fileName;
        file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ParkingLot readData() {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        try {
            if (file.length() == 0) {
                return new ParkingLot();
            }
            // Deserialize JSON file to ParkingLot object
            return objectMapper.readValue(file, ParkingLot.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeData(ParkingLot data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        try {
            // Serialize ParkingLot object to JSON file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
