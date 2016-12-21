package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    boolean register(String username, String password) throws RemoteException;
    boolean authenticate(String username, String password, String ip, String port) throws RemoteException;
    public Object onlineFriends(String username) throws RemoteException;
}
