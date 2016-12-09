package UI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UI extends Application {

    private static BorderPane root = new BorderPane();
    private static Node previousNode;

    @Override
    public void start(Stage stage) throws Exception {
        MenuBar menu = FXMLLoader.load(getClass().getResource("menu.fxml"));
        AnchorPane login = FXMLLoader.load(getClass().getResource("login.fxml"));

        root.setTop(menu);
        root.setCenter(login);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Messages");
        stage.setResizable(false);
        stage.getIcons().add(new Image(this.getClass().getResource("icon.png").toString()));
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Get the root - usually to get it and then change the content of it
     */
    public static BorderPane getRoot() {
        return root;
    }

    public static Node getPreviousNode() {
        return previousNode;
    }

    public static void setPreviousNode(Node ap) {
        previousNode = ap;
    }
}
