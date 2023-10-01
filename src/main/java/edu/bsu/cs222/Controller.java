package edu.bsu.cs222;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.net.URLConnection;


public class Controller{

    @FXML
    private TextField titleField;
    @FXML
    private Button retrieve;
    @FXML
    private TextArea outputText;


    public void retrieveWikipedia(ActionEvent actionEvent) {
        String title= titleField.getText();
        try {
            URLConnection connection=wikipediaLoader.connectToWikipedia(title);
            String jsonData=wikipediaLoader.readJsonAsStringFrom(connection);
            outputText.setText(jsonData);
        }catch(Exception e){
            System.out.println("Error has occurred!");
        }
    }
}
