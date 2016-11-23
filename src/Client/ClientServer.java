package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

public class ClientServer extends SwingWorker<List, List> {

    Socket socketClient;
    ServerSocket socketClientServer;
    String IP;
    String username;
    int port;
    private List<String> conversation = new ArrayList<String>();
    ObjectOutputStream out;
    ObjectInputStream in;

    public ClientServer(String username, String IP, int port) throws IOException {
        this.IP = IP;
        this.username = username;
        this.port = port;
        socketClientServer = new ServerSocket(this.port);
        System.out.println("Server: " + Inet4Address.getLocalHost().getHostAddress() + ":" + socketClientServer.getLocalPort());
    }

    public void send(String message, String IP, int port) throws IOException {
        Socket socket = new Socket(IP, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(message);
        //System.out.println("To: " + IP + ":" + port + ". Message: " + message);
        conversation.add(message);
        //out.close();
    }

    @Override
    protected List doInBackground() throws Exception {
        while (true) {
            try {
                //esperar pela ligacao  (instrucao bloqueante)
                Socket socket = socketClientServer.accept();
                //abertura da stream de entrada - Stream de texto
                in = new ObjectInputStream(socket.getInputStream());

                //ler a mensagem
                String message = (String)in.readObject();
                System.out.println("Received Message: " + message);
                conversation.add(message);

                //fechar o socket
                //socket.close();
                //fechar as streams
                //in.close();
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
