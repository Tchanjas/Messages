package Client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingWorker;

public class ClientServer extends SwingWorker<List, List> {

    Socket socketClient;
    ServerSocket socketClientServer;
    String IP;
    String username;
    static int port;
    private List<String> conversation = new ArrayList<>();

    public ClientServer(String username, String IP, int port) throws IOException {
        this.IP = IP;
        this.username = username;
        this.port = port;
        socketClientServer = new ServerSocket(this.port);
        System.out.println("Server: " + Inet4Address.getLocalHost().getHostAddress() + ":" + socketClientServer.getLocalPort());
    }

    public void send(String message, String IP, int port) throws IOException {
        ObjectOutputStream out;
        Socket socket = new Socket(IP, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(message);
        conversation.add(message);
        out.close();
    }

    @Override
    protected List doInBackground() throws Exception {
        while (true) {
            try {
                //esperar pela ligacao  (instrucao bloqueante)
                Socket socket = socketClientServer.accept();
                //abertura da stream de entrada - Stream de texto
                Scanner in = new Scanner(socket.getInputStream());
                //ler a mensagem
                String message = in.nextLine();
                //System.out.println("Received Message: " + message);
                conversation.add(message);
                //fechar o socket
                socket.close();
                //fechar as streams
                in.close();
            } // esperar por novos clientes
            catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public List<String> getConversation() {
        return conversation;
    }

    public void setConversation(List<String> conversation) {
        this.conversation = conversation;
    }
}
