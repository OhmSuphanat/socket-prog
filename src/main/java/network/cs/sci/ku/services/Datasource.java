package network.cs.sci.ku.services;

public interface Datasource<T> {
    T readData();
    void writeData(T data);
}
