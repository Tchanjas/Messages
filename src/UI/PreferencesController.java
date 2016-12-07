package UI;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class PreferencesController implements Initializable {

    @FXML
    private Button btSave;
    @FXML
    private Button btBack;
    @FXML
    private TextField txtClientPort;
    @FXML
    private TextField txtServerIP;
    @FXML
    private TextField txtServerPort;
    
    Properties configProperties = null;

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
            txtClientPort.setText(configProperties.getProperty("clientPort"));
            txtServerIP.setText(configProperties.getProperty("serverIP"));
            txtServerPort.setText(configProperties.getProperty("serverPort"));
        }
    }    

    @FXML
    private void saveAction(ActionEvent event) {
        // SAVE CONFIG HERE
        try {
            configProperties.setProperty("clientPort", txtClientPort.getText());
            configProperties.setProperty("serverIP", txtServerIP.getText());
            configProperties.setProperty("serverPort", txtServerPort.getText());
            FileOutputStream out = new FileOutputStream("config.properties");
            configProperties.store(out, "Configuration");
        } catch (Exception ex) {
        }
        
        // IF SUCCESS
        if (true) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Preferences Saved");
            alert.setHeaderText("Preferences Saved");
            alert.setContentText("The preferences were saved successfully");
            alert.showAndWait();
            
            // THEN GO BACK
            Node back = UI.getPreviousNode();
            BorderPane border = UI.getRoot();
            border.setCenter(back);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Preferences Error");
            alert.setHeaderText("Error Saving Preferences!");
            alert.setContentText("Something went wrong.");
            alert.showAndWait();
        }
    }

    @FXML
    private void backAction(ActionEvent event) {
        Node back = UI.getPreviousNode();
        BorderPane border = UI.getRoot();
        border.setCenter(back);
    }
}
