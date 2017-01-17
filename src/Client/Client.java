package Client;

import Utils.Crypto;
import Utils.Serializer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.Key;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements ClientInterface {

    String IP;
    String username;
    int port;
    static HashMap<String, Object[]> conversation = new HashMap<String, Object[]>();
    KeyPair keys = Crypto.generateKeypair("RSA");

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
    public void sendMessage(String message, String username, String IP, int port, HashMap users, Key sessionKey) throws RemoteException, AccessException {
        try {
            Registry registry = LocateRegistry.getRegistry(IP, port);
            ClientInterface stub = (ClientInterface) registry.lookup("Client");
            
            PublicKey userPub = (PublicKey) stub.getPublicKey();
            byte[] newMessage = Base64.getEncoder().encode(Crypto.cypher(message.getBytes(), sessionKey));
            byte[] newUsername = Base64.getEncoder().encode(Crypto.cypher(username.getBytes(), sessionKey));
            byte[] newUsers = Base64.getEncoder().encode(Crypto.cypher(Serializer.toBytes(users), sessionKey));
            byte[] key = Base64.getEncoder().encode(Crypto.cypher(Serializer.toBytes(sessionKey), userPub));
            stub.getMessage(newMessage, newUsername, newUsers, key);
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void getMessage(byte[] message, byte[] username, byte[] users, byte[] simetric) throws RemoteException {
        Key simKey = (Key) Serializer.toObject(Crypto.decypher(Base64.getDecoder().decode(simetric), keys.getPrivate()));
        String newMessage = new String(Crypto.decypher(Base64.getDecoder().decode(message), simKey));
        String newUsername = new String(Crypto.decypher(Base64.getDecoder().decode(username), simKey));
        HashMap newUsers = (HashMap) Serializer.toObject(Crypto.decypher(Base64.getDecoder().decode(users), simKey));
        if (conversation.get(newUsername) == null) {
            Object[] list = new Object[2];
            List<String> listMessages = new ArrayList<String>();
            listMessages.add(newMessage);
            list[0] = listMessages;
            list[1] = newUsers;
            conversation.put(newUsername, list);
        } else {
            Object[] list = new Object[2];
            List<String> listMessages = (List) conversation.get(newUsername)[0];
            listMessages.add(newMessage);
            list[0] = listMessages;
            HashMap listUsers = (HashMap) conversation.get(newUsername)[1];
            listUsers.putAll(newUsers);
            list[1] = listUsers;

            conversation.remove(newUsername);
            conversation.put(newUsername, list);
        }
    }

    public HashMap<String, Object[]> getConversation() {
        return conversation;
    }

    public void clearConversation() {
        conversation.clear();
    }

    @Override
    public boolean sendFile(File file, String ip, String port) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(ip, Integer.valueOf(port));
            ClientInterface stub = (ClientInterface) registry.lookup("Client");

            FileInputStream in = new FileInputStream(file);
            byte[] data = new byte[(int) file.length()];
            in.read(data);
            in.close();

            PublicKey friendKey = (PublicKey) stub.getPublicKey();
            Key simetric = Crypto.generateSessionKey("AES");

            byte[] signature = Base64.getEncoder().encode(Crypto.signFile(data, keys.getPrivate()));
            data = Base64.getEncoder().encode(Crypto.cypher(data, simetric));
            byte[] pubkey = Serializer.toBytes(keys.getPublic());
            String filename = Base64.getEncoder().encodeToString(file.getName().getBytes());
            byte[] simKey = Base64.getEncoder().encode(Crypto.cypher(Serializer.toBytes(simetric), friendKey));

            stub.receiveFile(data, signature, pubkey, filename, simKey);
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return false;

    }

    @Override
    public void receiveFile(byte[] file, byte[] signature, byte[] key, String filename, byte[] simetric) throws RemoteException {
        try {
            PublicKey pubKey = (PublicKey) Serializer.toObject(key);
            Key simKey = (Key) Serializer.toObject(Crypto.decypher(Base64.getDecoder().decode(simetric), keys.getPrivate()));

            filename = new String(Base64.getDecoder().decode(filename.getBytes()), "UTF-8");
            file = Crypto.decypher(Base64.getDecoder().decode(file), simKey);
            signature = Base64.getDecoder().decode(signature);

            if (Crypto.checkSign(file, pubKey, signature)) {
                FileOutputStream out = new FileOutputStream(filename);
                out.write(file);
                out.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Key getPublicKey() throws RemoteException {
        return keys.getPublic();
    }

}
