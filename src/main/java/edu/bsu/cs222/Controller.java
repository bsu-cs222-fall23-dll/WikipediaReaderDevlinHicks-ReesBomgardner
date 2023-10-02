package edu.bsu.cs222;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;


public class Controller {

    @FXML
    private TextField titleField;
    @FXML
    private Button retrieve;
    @FXML
    private TextArea outputText;


    public void retrieveWikipedia(ActionEvent actionEvent) {
        String title = titleField.getText();
        try {
            URLConnection connection = connectToWikipedia(title);
            String jsonData = readJsonAsStringFrom(connection);
            outputText.setText(jsonData);
            printRecentChanges(jsonData);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private URLConnection connectToWikipedia(String articleTitle) throws IOException {
        String urlString = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=revisions&titles=" +
                articleTitle +
                "&rvprop=timestamp|user&rvlimit=13&redirects";
        URL website = new URL(urlString);
        URLConnection connect = website.openConnection();
        connect.setRequestProperty("User-Agent",
                "CS222FirstProject/0.1 (devlin.hicks@bsu.edu)");
        connect.connect();
        return connect;
    }

    private String readJsonAsStringFrom(URLConnection connection) {
        try {
            InputStream dataStream = connection.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(dataStream, Charset.defaultCharset());
            StringBuilder String = new StringBuilder();
            char[] bugger = new char[1024];
            int bytesRead;
            while ((bytesRead = streamReader.read(bugger)) != -1) {
                String.append(bugger, 0, bytesRead);
            }
            return String.toString();

        } catch (IOException e) {
            System.err.println("Error: Error Detected while reading JSON Data!");
            e.getMessage();
            return "";
        }
    }

    private void printRecentChanges(String jsonData) {
        JsonObject jsonChanges = JsonParser.parseString(jsonData).getAsJsonObject();
        if (jsonChanges.has("query")) {
            JsonObject queries = jsonChanges.getAsJsonObject("query");

            if (queries.has("pages")) {
                JsonObject pages = queries.getAsJsonObject("pages");

                for (String pageId : pages.keySet()) {
                    JsonObject page = pages.getAsJsonObject(pageId);

                    if (page.has("revisions")) {
                        JsonArray revisions = page.getAsJsonArray("revisions");

                        if (revisions.size() == 0) {
                            outputText.appendText("No recent changes were found in: " + page.get("title") + "\n");
                        } else {
                            outputText.appendText("Recent changes found for: " + page.get("title") + "\n");
                            for (JsonElement revisionElement : revisions) {
                                JsonObject changeObject = revisionElement.getAsJsonObject();
                                String time = changeObject.get("timestamp").getAsString();
                                String users = changeObject.get("user").getAsString();
                                outputText.appendText("Time: " + time + " Users: " + users + "\n");
                            }
                        }
                    } else {
                        outputText.appendText("No recent changes found in " + page.get("title") + "\n");
                    }
                }
            } else {
                outputText.appendText("Query Data Not Found!\n");
            }
        } else {
            outputText.appendText("Query Data Not Found!\n");
        }
    }
}

