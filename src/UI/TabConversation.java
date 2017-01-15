package UI;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;

public class TabConversation extends Tab {

    private String clientUsername;
    private String username;
    private String IP;
    private int port;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private Date date;

    private Label labelConversation = new Label();
    private TextArea textAreaSend = new TextArea();
    private Button btSend = new Button();

    public TabConversation(String clientUsername, String username, String IP, int port) throws IOException {
        this.clientUsername = clientUsername;
        this.username = username;
        this.IP = IP;
        this.port = port;

        // tab content
        FlowPane conversationPane = new FlowPane();

        labelConversation.setAlignment(Pos.TOP_LEFT);
        labelConversation.setPrefHeight(300.0);
        labelConversation.setPrefWidth(400.0);
        textAreaSend.setPrefHeight(78.0);
        textAreaSend.setPrefWidth(395.0);

        btSend.setText("Send");
        btSend.setOnAction(e -> sendAction(e));
        btSend.setPrefHeight(78.0);
        btSend.setPrefWidth(50.0);

        conversationPane.getChildren().add(labelConversation);
        conversationPane.getChildren().add(textAreaSend);
        conversationPane.getChildren().add(btSend);

        this.setContent(conversationPane);

        // on tab close
        this.setOnCloseRequest(e -> closeTab());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLabelConversationText() {
        return labelConversation.getText();
    }

    public void setLabelConversationText(String text) {
        labelConversation.setText(text);
    }

    private void sendAction(ActionEvent event) {
        try {
            date = new Date();
            // send a message = message, destination username, source username, destination IP, destination port
            MainController.client.sendMessage(dateFormat.format(date) + " - " + clientUsername + ":" + textAreaSend.getText(),
                    username, IP, port);
            textAreaSend.setText("");
        } catch (Exception ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void closeTab() {
        MainController.closeTab(username);
    }
}
