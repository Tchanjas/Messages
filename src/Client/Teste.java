package Client;

import Server.AuthServer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Teste {

    public static void main(String[] args) {
        try {
            AuthServer servidor = new AuthServer(10000);
            ClientServer client = new ClientServer("username", "127.0.0.1", 5555);
            client.send("register,bruno,hash", "127.0.0.1", 10000);
            client.send("register,bruno,hash", "127.0.0.1", 10000);
        } catch (Exception ex) {
            Logger.getLogger(Teste.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
