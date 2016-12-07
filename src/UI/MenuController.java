package UI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MenuController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void preferencesAction(ActionEvent event) throws IOException {
        AnchorPane signup = FXMLLoader.load(getClass().getResource("preferences.fxml"));
        UI.setPreviousNode(UI.getRoot().getCenter());
        BorderPane border = UI.getRoot();
        border.setCenter(signup);
    }
    
    @FXML
    private void exitAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void aboutAction(ActionEvent event) throws IOException {
            AnchorPane about = FXMLLoader.load(getClass().getResource("about.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("About");
            stage.setResizable(false);
            stage.setScene(new Scene(about));
            stage.show();
    }

}
