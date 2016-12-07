package UI;

import Client.ClientServer;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    private Button btConnect;
    @FXML
    private Button btSend;

    ClientServer client;
    static String username;
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date;

    Properties configProperties = null;

    String clientPort = null;

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
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), getConv -> getConversation()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void connectAction(ActionEvent event) {
        try {
            client = new ClientServer(username, "localhost", 10010);
            new Thread(client).start();
        } catch (IOException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void sendAction(ActionEvent event) {
        try {
            date = new Date();
            client.send(dateFormat.format(date) + " - " + username + ":" + tempTextArea.getText(),
                    txtIP.getText(), Integer.parseInt(txtPort.getText()));
            tempTextArea.setText("");
        } catch (IOException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
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
