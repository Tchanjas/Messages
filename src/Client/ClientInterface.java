package Client;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Key;
import java.util.HashMap;

public interface ClientInterface extends Remote {
    void sendMessage(String message, String username, String IP, int port, HashMap users, Key sessionKey) throws RemoteException;
    void getMessage(String message, String username, HashMap users, byte[] simetric) throws RemoteException;
    boolean sendFile(File file, String ip, String port) throws RemoteException;
    void receiveFile(byte [] file, byte [] signature, byte [] key, String filename, byte[] simetric) throws RemoteException;
    Key getPublicKey() throws RemoteException;
}
