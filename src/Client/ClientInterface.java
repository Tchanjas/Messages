package Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    void sendMessage(String message, String IP, int port) throws RemoteException;
    void getMessage(String message, String IP, int port) throws RemoteException;
}