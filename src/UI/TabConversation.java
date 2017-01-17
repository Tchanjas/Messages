package UI;

import Utils.Crypto;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.Key;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;

public class TabConversation extends Tab {

    private String clientUsername;
    private String tabTitle;
    HashMap<String, List<String>> users = new HashMap();

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Date date;

    private Label labelConversation = new Label();
    private TextArea textAreaSend = new TextArea();
    private Button btSendMessage = new Button();
    private Button btSendFile = new Button();

    Key sessionKey = Crypto.generateSessionKey("AES");

    public TabConversation(String clientUsername, String clientIP, String clientPort, String username, String IP, int port) throws IOException {
        this.clientUsername = clientUsername;
        this.tabTitle = username;
        List<String> list = new ArrayList();
        list.add(clientUsername);
        list.add(clientIP);
        list.add(clientPort);
        users.put(clientUsername, list);

        list = new ArrayList();
        list.add(username);
        list.add(IP);
        list.add(port + "");
        users.put(username, list);

        // tab content
        FlowPane conversationPane = new FlowPane();

        labelConversation.setAlignment(Pos.TOP_LEFT);
        labelConversation.setPrefHeight(300.0);
        labelConversation.setPrefWidth(400.0);
        textAreaSend.setPrefHeight(78.0);
        textAreaSend.setPrefWidth(325.0);

        btSendMessage.setText("Send \n Message");
        btSendMessage.setOnAction(e -> sendMessageAction(e));
        btSendMessage.setPrefHeight(78.0);
        btSendMessage.setPrefWidth(70.0);

        btSendFile.setText("Send \n File");
        btSendFile.setOnAction(e -> sendFileAction(e));
        btSendFile.setPrefHeight(78.0);
        btSendFile.setPrefWidth(50.0);

        conversationPane.getChildren().add(labelConversation);
        conversationPane.getChildren().add(textAreaSend);
        conversationPane.getChildren().add(btSendMessage);
        conversationPane.getChildren().add(btSendFile);

        this.setContent(conversationPane);

        // on tab close
        this.setOnCloseRequest(e -> closeTab());
    }

    public String getLabelConversationText() {
        return labelConversation.getText();
    }

    public void setLabelConversationText(String text) {
        labelConversation.setText(text);
    }

    private void sendMessageAction(ActionEvent event) {
        try {
            String usersConcat = "";
            for (HashMap.Entry<String, List<String>> entry : users.entrySet()) {
                if (usersConcat.isEmpty()) {
                    usersConcat = entry.getKey();
                } else {
                    usersConcat = usersConcat + "," + entry.getKey();
                }
            }

            date = new Date();
            // send for each user in the tab
            // each message = message, concat of usernames, destination IP, destination port
            for (HashMap.Entry<String, List<String>> entry : users.entrySet()) {
                String entryTabTitle = usersConcat.replaceAll(entry.getValue().get(0) + ",", "");
                entryTabTitle = entryTabTitle.replaceAll("," + entry.getValue().get(0), "");
                
                MainController.client.sendMessage(dateFormat.format(date) + " - " + clientUsername + ":" + textAreaSend.getText(),
                        entryTabTitle, entry.getValue().get(1), Integer.parseInt(entry.getValue().get(2)), users, sessionKey);
            }

            textAreaSend.setText("");
        } catch (Exception ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendFileAction(ActionEvent event) {
        String usersConcat = "";
        for (HashMap.Entry<String, List<String>> entry : users.entrySet()) {
            if (usersConcat.isEmpty()) {
                usersConcat = entry.getKey();
            } else {
                usersConcat = usersConcat + "," + entry.getKey();
            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(UI.getRoot().getScene().getWindow());

        date = new Date();

        for (HashMap.Entry<String, List<String>> entry : users.entrySet()) {
            if (!entry.getKey().equals(clientUsername)) {
                try {
                    String entryTabTitle = usersConcat.replaceAll(entry.getValue().get(0) + ",", "");
                    entryTabTitle = entryTabTitle.replaceAll("," + entry.getValue().get(0), "");

                    MainController.client.sendMessage("File sent from " + clientUsername,
                            entryTabTitle, entry.getValue().get(1), Integer.parseInt(entry.getValue().get(2)), users, sessionKey);

                    MainController.client.sendFile(file, entry.getValue().get(1), entry.getValue().get(2));

                    MainController.client.sendMessage("File received from " + clientUsername,
                            entryTabTitle, entry.getValue().get(1), Integer.parseInt(entry.getValue().get(2)), users, sessionKey);
                } catch (RemoteException ex) {
                    Logger.getLogger(TabConversation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void closeTab() {
        MainController.closeTab(tabTitle);
    }

    void addUser(String username, String IP, int port) {
        this.tabTitle = this.tabTitle + "," + username;
        List<String> list = new ArrayList();
        list.add(username);
        list.add(IP);
        list.add(port + "");
        users.put(username, list);
        this.setText(this.tabTitle);
    }
}
