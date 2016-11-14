package Client;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

public class ClientServer extends SwingWorker<List, List> {

    Socket socketClient;
    ServerSocket socketClientServer;
    String IP;
    int port;
    private List<String> conversation = new ArrayList<String>();

    public ClientServer(String IP, int port) throws IOException {
        this.IP = IP;
        this.port = port;
        ClientServer(this.port);
    }

    public void ClientServer(int port) throws IOException {
        socketClientServer = new ServerSocket(port);
        System.out.println("Server: " + Inet4Address.getLocalHost().getHostAddress() + ":" + socketClientServer.getLocalPort());
    }

    public void send(String message, String IP, int port) throws IOException {
        PrintStream out;
        Socket socket = new Socket(IP, port);
        out = new PrintStream(socket.getOutputStream());
        out.println(message);
        System.out.println("To: " + IP + ":" + port + ". Message: " + message);
        conversation.add("To: " + IP + ":" + port + ". Message: " + message);
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

                System.out.println("Received Message: " + message);
                conversation.add("Received Message: " + message);

                //fechar o socket
                socket.close();
                //fechar as streams
                in.close();
            } // esperar por novos clientes
            catch (IOException ex) {
                Logger.getLogger(ClientServer.class.getName()).log(Level.SEVERE, null, ex);
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
