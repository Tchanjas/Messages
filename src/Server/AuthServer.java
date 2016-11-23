package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthServer implements Runnable {

    String dbDriver = "org.apache.derby.jdbc.ClientDriver";
    String dbUrl = "jdbc:derby://localhost:1527/Messages;autoReconnect=true";

    ServerSocket listener;
    ObjectOutputStream out;
    ObjectInputStream in;

    public AuthServer(int port) {
        try {
            this.listener = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(AuthServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        System.out.println("Authentication Server is running");
        while (true) {
            try {
                Socket socket = listener.accept();
                Thread thr = new Server.ThreadAuth(socket);
                thr.start();
            } catch (IOException ex) {
                Logger.getLogger(AuthServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
