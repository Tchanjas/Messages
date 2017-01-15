package UI;

import Server.ServerInterface;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LoginController implements Initializable {

    @FXML
    private Button btSignin;
    @FXML
    private TextField txtUser;
    @FXML
    private Button btSignup;
    @FXML
    private PasswordField pfPass;

    Properties configProperties = null;
    String serverIP = null;
    String serverPort = null;
    String clientPort = null;

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
            clientPort = configProperties.getProperty("clientPort");
        }
    }

    @FXML
    private void signinAction(ActionEvent event) {
        try {
            Registry registry = LocateRegistry.getRegistry(serverIP, Integer.parseInt(serverPort));
            ServerInterface stub = (ServerInterface) registry.lookup("Server");
            boolean response = stub.authenticate(txtUser.getText(), pfPass.getText(), 
                    Inet4Address.getLocalHost().getHostAddress().toString(), clientPort);
            
            if (response == true) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Authentication Successful");
                alert.setHeaderText("Sign in successfully");
                alert.setContentText("Welcome " + txtUser.getText());
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
                alert.showAndWait();

                MainController.setUsername(txtUser.getText());

                AnchorPane main = FXMLLoader.load(getClass().getResource("main.fxml"));
                BorderPane border = UI.getRoot();
                border.setCenter(main);
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Authentication Error");
                alert.setHeaderText("Authentication Error!");
                alert.setContentText("Something went wrong. \n Verify if your credentials are correct.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
                alert.showAndWait();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    private void signupAction(ActionEvent event) throws IOException {
        AnchorPane signup = FXMLLoader.load(getClass().getResource("signup.fxml"));
        BorderPane border = UI.getRoot();
        border.setCenter(signup);
    }

}
