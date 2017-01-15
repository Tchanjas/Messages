package UI;

import Client.Client;
import Server.ServerInterface;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

public class MainController implements Initializable {

    @FXML
    private ListView<String> list = new ListView<>();
    @FXML
    private TabPane tabPane;

    static Client client;
    static String username;

    Properties configProperties = null;

    String clientPort = null;
    String serverIP = null;
    String serverPort = null;

    ServerInterface stub = null;

    HashMap friendsList = new HashMap();
    HashMap<String, List<String>> conversation = new HashMap<>();
    static HashMap<String, TabConversation> tabs = new HashMap<>();

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
            client = new Client(username, "localhost", Integer.parseInt(clientPort));
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
                        try {
                            setConversation();
                        } catch (IOException ex) {
                            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
    private void listAction(MouseEvent event) throws IOException {
        if (tabs.get(list.getSelectionModel().getSelectedItem()) == null) {
            String selected = list.getSelectionModel().getSelectedItem();

            TabConversation tab = new TabConversation(username, selected,
                    friendsList.get(selected).toString().split(":")[0],
                    Integer.parseInt(friendsList.get(selected).toString().split(":")[1]));
            tab.setText(selected);

            tabPane.getTabs().add(tab);
            tabs.put(selected, tab);
        }
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        MainController.username = username;
    }

    private void getConversation() {
        if (client.getConversation() != null && !client.getConversation().isEmpty()) {
            for (HashMap.Entry<String, List<String>> entry : client.getConversation().entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    if (conversation.get(entry.getKey()) == null) {
                        conversation.put(entry.getKey(), entry.getValue());
                    } else {
                        conversation.get(entry.getKey()).addAll(entry.getValue());
                    }
                }
            }
        }
        client.clearConversation();
    }

    private void setConversation() throws IOException {
        if (!conversation.isEmpty()) {
            for (HashMap.Entry<String, List<String>> entry : conversation.entrySet()) {
                if (tabs.get(entry.getKey()) == null) {
                    String incomingUsername = entry.getKey();

                    TabConversation tab = new TabConversation(username, incomingUsername,
                            friendsList.get(incomingUsername).toString().split(":")[0],
                            Integer.parseInt(friendsList.get(incomingUsername).toString().split(":")[1]));
                    tab.setText(incomingUsername);

                    tabPane.getTabs().add(tab);
                    tabs.put(incomingUsername, tab);
   
                    for (String item : entry.getValue()) {
                        tab.setLabelConversationText(tab.getLabelConversationText() + "\n" + item);
                    }
                } else {
                    TabConversation tab = tabs.get(entry.getKey());
                    for (String item : entry.getValue()) {
                        tab.setLabelConversationText(tab.getLabelConversationText() + "\n" + item);
                    }
                }
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

    static void closeTab(String username) {
        tabs.remove(username);
    }
}
