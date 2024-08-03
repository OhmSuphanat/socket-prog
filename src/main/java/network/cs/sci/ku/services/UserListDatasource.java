package network.cs.sci.ku.services;

import network.cs.sci.ku.models.UserList;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class UserListDatasource implements Datasource<UserList>{
    private String directoryName;
    private String fileName;
    private ObjectMapper objectMapper;

    public UserListDatasource(String directoryName, String fileName) {
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
    public UserList readData() {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        try {
            if (file.length() == 0) {
                return new UserList();
            }
            // Deserialize JSON file to UserList object
            return objectMapper.readValue(file, UserList.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeData(UserList data) {
        String filePath = directoryName + File.separator + fileName;
        File file = new File(filePath);

        try {
            // Serialize UserList object to JSON file
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
