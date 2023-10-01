package edu.bsu.cs222;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
public class WikipediaLoaderGUI extends Application {
    public static void main(String [] args){
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader load=new FXMLLoader(getClass().getResource("java/edu/bsu/cs222/scratch.fxml"));
        Parent root=load.load();
        Controller control=load.getController();
        primaryStage.setTitle("Wikipedia Revision Loader");
        primaryStage.setScene(new Scene(root,800,600));
        primaryStage.show();
    }
}
