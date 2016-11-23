package Client;

import Server.AuthServer;

public class Teste {

    public static void main(String[] args) {
        AuthServer servidor = new AuthServer(10000);
        new Thread(servidor).start();
    }
}
