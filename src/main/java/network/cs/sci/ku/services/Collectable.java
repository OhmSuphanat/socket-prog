package network.cs.sci.ku.services;

public interface Collectable<T>{
    void addObject(T object);
    T findObjectByPK(String primaryKey);
}
