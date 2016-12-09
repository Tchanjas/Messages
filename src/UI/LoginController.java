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
    private void signinAction(ActionEvent event) {
        try {
            Socket socket = new Socket(serverIP, Integer.parseInt(serverPort));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            out.writeObject("authenticate," + txtUser.getText() + "," + String.valueOf(pfPass.getText()).hashCode());
            out.flush();
            String response = (String) in.readObject();
            if (response.startsWith("3")) {
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
                System.out.println(response.toString());
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Authentication Error");
                alert.setHeaderText("Authentication Error!");
                alert.setContentText("Something went wrong. \n Verify if your credentials are correct.");
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
                alert.showAndWait();
            }
            out.close();
            in.close();
        } catch (Exception ex) {
        }
    }

    @FXML
    private void signupAction(ActionEvent event) throws IOException {
        AnchorPane signup = FXMLLoader.load(getClass().getResource("signup.fxml"));
        BorderPane border = UI.getRoot();
        border.setCenter(signup);
    }

}