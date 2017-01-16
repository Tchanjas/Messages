package UI;

import Server.ServerInterface;
import Utils.Crypto;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.Key;
import java.util.Base64;
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
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SignupController implements Initializable {

    @FXML
    private PasswordField pfPass;
    @FXML
    private TextField txtUser;
    @FXML
    private Button btBack;
    @FXML
    private Button btSignup;

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
            Registry registry = LocateRegistry.getRegistry(serverIP, Integer.parseInt(serverPort));
            ServerInterface stub = (ServerInterface) registry.lookup("Server");

            Key serverKey = stub.getPublicKey();

            String hash = Utils.BCrypt.hashpw(pfPass.getText(), Utils.BCrypt.gensalt());
            String encodUser = Base64.getEncoder().encodeToString(Crypto.cypher(txtUser.getText().getBytes(), serverKey));
            String encodPass = Base64.getEncoder().encodeToString(Crypto.cypher(hash.getBytes(), serverKey));
            boolean response = stub.register(encodUser, encodPass);

            if (response == true) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Registration Successful");
                alert.setHeaderText("Sign up successfully");
                alert.setContentText("Sign up successfully");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
                alert.showAndWait();

                AnchorPane login = FXMLLoader.load(getClass().getResource("login.fxml"));
                BorderPane border = UI.getRoot();
                border.setCenter(login);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Authentication Error");
                alert.setHeaderText("Authentication Error!");
                alert.setContentText("Something went wrong. \n Verify if your credentials are correct.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
                alert.showAndWait();
            }
        } catch (Exception ex) {
        }
    }

}
