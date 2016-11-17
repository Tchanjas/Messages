package Client;

import Server.AuthServer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Teste {

    public static void main(String[] args) {
        try {
            AuthServer servidor = new AuthServer(10000);
            new Thread(servidor).start();
            ClientServer client = new ClientServer("username", "127.0.0.1", 5555);
            client.send("register,bruno,hash,"+client.port, "127.0.0.1", 10000);
            client.send("register,andre,hash,"+client.port, "127.0.0.1", 10000);
            client.send("register,ricardo,hash,"+client.port, "127.0.0.1", 10000);
        } catch (Exception ex) {
            Logger.getLogger(Teste.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
