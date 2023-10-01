package edu.bsu.cs222;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class wikipediaLoaderGUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("src/main/resources/scratch.fxml"));
            Parent root = loader.load();
            Controller control = loader.getController();
            primaryStage.setTitle("Wikipedia Revision Loader");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error: "+e.getMessage());
        }
    }
}
