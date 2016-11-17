package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthServer implements Runnable {

    ServerSocket listener;
    /**
     * TODO - O hashmap tem de ser concorrente Copyonwrite
     */
    HashMap<String, ArrayList<String>> users;
    ObjectOutputStream out;
    ObjectInputStream in;

    public AuthServer(int port) {
        try {
            this.listener = new ServerSocket(port);
            users = new HashMap<>();
        } catch (IOException ex) {
            Logger.getLogger(AuthServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void register(String username, String hash, Socket socket, int port) {
        if (!users.containsKey(username)) {
            ArrayList<String> aux = new ArrayList<>();
            aux.add(hash);
            aux.add(socket.getInetAddress().toString());
            aux.add(port + "");
            users.put(username, aux);
        } else {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject("Username already taken");
                out.flush();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(AuthServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public HashMap<String, ArrayList<String>> getUsers() {
        return users;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = listener.accept();
                System.out.println("connection done");
                in = new ObjectInputStream(socket.getInputStream());
                String readObj = (String) in.readObject();
                String[] fields = readObj.split(",");
                String username = fields[1];
                String hashPass = fields[2];
                int port = Integer.parseInt(fields[3]);
                if (readObj.startsWith("register")) {
                    register(username, hashPass, socket, port);
                    System.out.println(users.toString());
                } else if (readObj.startsWith("login")) {
                    if (users.get(username).get(0) == hashPass) {
                        System.out.println("Login done");
                    } else {
                        System.out.println("User already exists");
                    }
                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
