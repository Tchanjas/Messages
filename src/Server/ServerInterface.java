package Server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ServerInterface extends Remote {
    boolean register(String username, String password) throws RemoteException;
    boolean authenticate(String username, String password, String ip, String port) throws RemoteException;
    boolean addFriend(String user, String friend) throws RemoteException;
    public HashMap onlineFriends(String username) throws RemoteException;
}
