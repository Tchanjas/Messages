package Client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ClientInterface extends Remote {
    void sendMessage(String message, String username, String IP, int port, HashMap users) throws RemoteException;
    void getMessage(String message, String username, HashMap users) throws RemoteException;
}
