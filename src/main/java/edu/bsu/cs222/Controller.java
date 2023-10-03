package edu.bsu.cs222;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Platform;
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
    private TextArea outputText;


    public void retrieveWikipedia(ActionEvent actionEvent) {
        String title = titleField.getText();
        if(title.isBlank()){
            missingName();
        }

        try {
            URLConnection connection = connectToWikipedia(title);
            String jsonData = readJsonAsStringFrom(connection);
            boolean existence= articleExistence(jsonData);
            if (existence) {
                outputText.appendText(title+" exists\n");
            } else {
                outputText.appendText(title+ " does not exist.\n");
            }

            String redirectTarget = redirectTester(jsonData);
            if (redirectTarget != null) {
                outputText.appendText("Redirected to: " + redirectTarget + "\n");
            }
            printRecentChanges(jsonData);
        } catch (IOException e) {
            networkError();
        }
    }


    public static String redirectTester(String jsonData) {
        JsonObject CheckObject = JsonParser.parseString(jsonData).getAsJsonObject();
        if (CheckObject.has("query")) {
            JsonObject jsonQueryObject = CheckObject.getAsJsonObject("query");
            if (jsonQueryObject.has("redirects")) {
                JsonArray redirectArray = jsonQueryObject.getAsJsonArray("redirects");
                if (!redirectArray.isEmpty()) {
                    JsonObject recentRedirects = redirectArray.get(0).getAsJsonObject();
                    return recentRedirects.get("to").getAsString();
                }
            }
        }
        return null;
    }

    public void networkError() {
        Platform.runLater(() -> {
            outputText.appendText("Network Error has been detected!\n");
        });
    }

    public void missingName() {
        Platform.runLater(() -> {
            outputText.appendText("Article Name not given!\n");
        });
    }


    public static boolean articleExistence(String jsonData) {
        JsonObject jsonArticle = JsonParser.parseString(jsonData).getAsJsonObject();
        if (jsonArticle.has("query")) {
            JsonObject articleQuery = jsonArticle.getAsJsonObject("query");
            if (articleQuery.has("pages")) {
                JsonObject pages = articleQuery.getAsJsonObject("pages");
                return pages.keySet().stream().map(pages::getAsJsonObject).findFirst().filter(page -> !page.has("missing")).isPresent();
            }
        }
        return false;
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
            InputStream stream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(stream, Charset.defaultCharset());
            StringBuilder String = new StringBuilder();
            char[] bugger = new char[1024];
            int bytesRead;
            while ((bytesRead = reader.read(bugger)) != -1) {
                String.append(bugger, 0, bytesRead);
            }
            return String.toString();

        } catch (IOException e) {
            System.err.println("Error: Error Detected while reading JSON Data!");
            return "";
        }
    }

    private void printRecentChanges(String jsonData) {
        JsonObject changes = JsonParser.parseString(jsonData).getAsJsonObject();
        if (changes.has("query")) {
            JsonObject queries = changes.getAsJsonObject("query");

            if (queries.has("pages")) {
                JsonObject pageQuery = queries.getAsJsonObject("pages");

                for (String pageId : pageQuery.keySet()) {
                    JsonObject page = pageQuery.getAsJsonObject(pageId);

                    if (page.has("revisions")) {
                        JsonArray revisionQuery = page.getAsJsonArray("revisions");

                        if (revisionQuery.isEmpty()) {
                            outputText.appendText("No recent changes were found in: " + page.get("title") + "\n");
                        } else {
                            outputText.appendText("Recent changes found for: " + page.get("title") + "\n");
                            for (JsonElement revisionElement : revisionQuery) {
                                JsonObject changeObject = revisionElement.getAsJsonObject();
                                String time = changeObject.get("timestamp").getAsString();
                                String users = changeObject.get("user").getAsString();
                                outputText.appendText("Time: " + time + " User(s): " + users + "\n");
                            }
                        }
                    } else {
                        outputText.appendText("No recent changes were found for " + page.get("title") + "\n");
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

