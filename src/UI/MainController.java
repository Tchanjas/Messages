package UI;

import Client.Client;
import Server.ServerInterface;
import java.io.FileInputStream;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class MainController implements Initializable {

    @FXML
    private Label tempLabel;
    @FXML
    private TextArea tempTextArea;
    @FXML
    private TextField txtPort;
    @FXML
    private TextField txtIP;
    @FXML
    private Button btSend;
    @FXML
    private ListView<String> list = new ListView<>();

    Client client;
    static String username;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date;

    Properties configProperties = null;

    String clientPort = null;
    String serverIP = null;
    String serverPort = null;

    ServerInterface stub = null;

    List<String> conversation = new ArrayList<String>();
    HashMap friendsList = new HashMap();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FileInputStream in = null;

        try {
            // create and load default properties
            configProperties = new Properties();
            in = new FileInputStream("config.properties");
            configProperties.load(in);
            in.close();
        } catch (Exception ex) {
        } finally {
            try {
                in.close();
            } catch (Exception ex) {
            }
        }

        if (configProperties != null) {
            clientPort = configProperties.getProperty("clientPort");
            serverIP = configProperties.getProperty("serverIP");
            serverPort = configProperties.getProperty("serverPort");
        }

        try {
            /*
            * ===============================
            * TEMPORARY WAY FOR LOCAL TESTING
            * ===============================
             */
            client = new Client(username, "localhost", 10011);
            clientPort = "10011";
            /* ============================== */
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Registry registry = LocateRegistry.getRegistry(serverIP, Integer.parseInt(serverPort));
            stub = (ServerInterface) registry.lookup("Server");
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        // get and set periodically the conversation
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getConversation();
                Platform.runLater(new Runnable() {
                    public void run() {
                        setConversation();
                    }
                });
            }
        }, 0, 250);
        
        // get and set periodically the friends list
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getOnlineFriends();
                Platform.runLater(new Runnable() {
                    public void run() {
                        setOnlineFriends();
                    }
                });
            }
        }, 0, 5000);
    }

    @FXML
    private void sendAction(ActionEvent event) {
        try {
            date = new Date();
            client.sendMessage(dateFormat.format(date) + " - " + username + ":" + tempTextArea.getText(),
                    txtIP.getText(), Integer.parseInt(txtPort.getText()));
            tempTextArea.setText("");
        } catch (Exception ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void listAction(MouseEvent event) {
        String selected = list.getSelectionModel().getSelectedItem();
        System.out.println(selected + " : " + friendsList.get(selected));
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        MainController.username = username;
    }

    private void getConversation() {
        if (!client.getConversation().isEmpty()) {
            conversation.addAll(client.getConversation());
            client.clearConversation();
        }
    }

    private void setConversation() {
        if (!conversation.isEmpty()) {
            for (String item : conversation) {
                tempLabel.setText(tempLabel.getText() + "\n" + item);
            }
            conversation.clear();
        }
    }

    private void getOnlineFriends() {
        try {
            friendsList = stub.onlineFriends(username);
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setOnlineFriends() {
        ObservableList<String> items = FXCollections.observableArrayList(friendsList.keySet());
        list.setItems(items);
    }
}
