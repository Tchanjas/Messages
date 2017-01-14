package UI;

import Client.Client;
import Server.ServerInterface;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
            client = new Client(username, "localhost", 10010);
            /* ============================== */
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), getConv -> getConversation()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // get friends and put them in the sidebar list
        Registry registry;
        try {
            registry = LocateRegistry.getRegistry(serverIP, Integer.parseInt(serverPort));
            ServerInterface stub = (ServerInterface) registry.lookup("Server");
            friendsList = stub.onlineFriends(username);
            ObservableList<String> items = FXCollections.observableArrayList(friendsList.keySet());
            list.setItems(items);
        } catch (Exception ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        System.out.println(selected + " : " +  friendsList.get(selected));
    }

    private void getConversation() {
        try {
            List<String> conv = client.getConversation();
            for (String item : conv) {
                tempLabel.setText(tempLabel.getText() + "\n" + item);
            }
            conv.clear();
            client.setConversation(conv);
        } catch (Exception e) {
        }
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        MainController.username = username;
    }
}
