package UI;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class SignupController implements Initializable {

    @FXML
    private PasswordField pfPass;
    @FXML
    private TextField txtUser;
    @FXML
    private Button btBack;
    @FXML
    private Button btSignup;

    ObjectOutputStream out;
    ObjectInputStream in;
    Properties configProperties = null;
    String serverIP = null;
    String serverPort = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        FileInputStream fileInput = null;
        try {
            // create and load default properties
            configProperties = new Properties();
            fileInput = new FileInputStream("config.properties");
            configProperties.load(fileInput);
            fileInput.close();
        } catch (Exception ex) {
        } finally {
            try {
                fileInput.close();
            } catch (Exception ex) {
            }
        }

        if (configProperties != null) {
            serverIP = configProperties.getProperty("serverIP");
            serverPort = configProperties.getProperty("serverPort");
        }
    }

    @FXML
    private void backAction(ActionEvent event) throws IOException {
        AnchorPane login = FXMLLoader.load(getClass().getResource("login.fxml"));
        BorderPane border = UI.getRoot();
        border.setCenter(login);
    }

    @FXML
    private void signupAction(ActionEvent event) {
        try {
            Socket socket = new Socket(serverIP, Integer.parseInt(serverPort));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject("register," + txtUser.getText() + "," + String.valueOf(pfPass.getText()).hashCode());
            out.flush();
            String response = (String) in.readObject();
            if (response.startsWith("1")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText("Sign up successfully");
                alert.setContentText("Sign up successfully");
                alert.showAndWait();

                AnchorPane login = FXMLLoader.load(getClass().getResource("login.fxml"));
                BorderPane border = UI.getRoot();
                border.setCenter(login);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Authentication Error");
                alert.setHeaderText("Authentication Error!");
                alert.setContentText("Something went wrong. \n Verify if your credentials are correct.");
                alert.showAndWait();
            }
            out.close();
            in.close();
        } catch (Exception ex) {
        }
    }

}
