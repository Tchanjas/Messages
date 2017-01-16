package Client;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements ClientInterface {

    String IP;
    String username;
    int port;
    static HashMap<String, Object[]> conversation = new HashMap<String, Object[]>();

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
    public void sendMessage(String message, String username, String IP, int port, HashMap users) throws RemoteException, AccessException {
        try {
            Registry registry = LocateRegistry.getRegistry(IP, port);
            ClientInterface stub = (ClientInterface) registry.lookup("Client");
            stub.getMessage(message, username, users);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void getMessage(String message, String username, HashMap users) throws RemoteException {
        if (conversation.get(username) == null) {
            Object[] list = new Object[2];
            List<String> listMessages = new ArrayList<String>();
            listMessages.add(message);
            list[0] = listMessages;
            list[1] = users;
            conversation.put(username, list);
        } else {
            Object[] list = new Object[2];
            List<String> listMessages = (List) conversation.get(username)[0];
            listMessages.add(message);
            list[0] = listMessages;
            HashMap listUsers = (HashMap) conversation.get(username)[1];
            listUsers.putAll(users);
            list[1] = listUsers;

            conversation.remove(username);
            conversation.put(username, list);
        }
    }

    public HashMap<String, Object[]> getConversation() {
        return conversation;
    }

    public void clearConversation() {
        conversation.clear();
    }
}
