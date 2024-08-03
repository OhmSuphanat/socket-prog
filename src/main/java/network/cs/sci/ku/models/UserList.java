package network.cs.sci.ku.models;

import network.cs.sci.ku.services.Collectable;

import java.io.Serializable;
import java.util.ArrayList;

public class UserList implements Serializable, Collectable<User> {
    private ArrayList<User> users;

    public UserList() {
        users = new ArrayList<>();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    @Override
    public void addObject(User object) {
        User exist = findObjectByPK(object.getParkingID());
        if (exist == null) {
            users.add(object);
        }
    }

    public void removeUser(User user) {
        String userID  = user.getParkingID();
        User exist = findObjectByPK(userID);
        if (exist != null) {
            users.remove(exist);
        }
    }

    @Override
    public User findObjectByPK(String primaryKey) {
        for (User user : users) {
            if (user.isID(primaryKey)) {
                return user;
            }
        }
        return null;
    }
}
