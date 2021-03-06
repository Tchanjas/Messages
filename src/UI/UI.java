package UI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UI extends Application {

    private static BorderPane root = new BorderPane();
    private static Node previousNode; // so we can go back to the previous node
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
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

        // close the application
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });
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
    
    public static Stage getStage() { return stage; }

    public static Node getPreviousNode() {
        return previousNode;
    }

    public static void setPreviousNode(Node ap) {
        previousNode = ap;
    }
}
