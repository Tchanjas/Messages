package Client;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements ClientInterface {

    String IP;
    String username;
    int port;
    static HashMap<String, List<String>> conversation = new HashMap<String, List<String>>();

    private Client() {
    }

    public Client(String username, String IP, int port) throws IOException, RemoteException, AlreadyBoundException {
        this.IP = IP;
        this.username = username;
        this.port = port;

        Client obj = new Client();
        ClientInterface stub = (ClientInterface) UnicastRemoteObject.exportObject(obj, 0);
        // Bind the remote object's stub in the registry
        Registry registry = LocateRegistry.createRegistry(port);
        registry.bind("Client", stub);
    }

    @Override
    public void sendMessage(String message, String username, String IP, int port) throws RemoteException, AccessException {
        try {
            Registry registry = LocateRegistry.getRegistry(IP, port);
            ClientInterface stub = (ClientInterface) registry.lookup("Client");
            stub.getMessage(message, this.username, this.IP, this.port);
            
            if (conversation.get(username) == null) {
                List<String> list = new ArrayList<String>();
                list.add(message);
                conversation.put(username, list);
            } else {
                conversation.get(username).add(message);
            }
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void getMessage(String message, String username, String IP, int port) throws RemoteException {
        if (conversation.get(username) == null) {
            List<String> list = new ArrayList<String>();
            list.add(message);
            conversation.put(username, list);
        } else {
            conversation.get(username).add(message);
        }
    }

    public HashMap<String, List<String>> getConversation() {
        return conversation;
    }

    public void clearConversation() {
        conversation.clear();
    }
}
