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
        if(title.isBlank()){
            MissingNameHandler();
        }

        try {
            URLConnection connection = connectToWikipedia(title);
            String jsonData = readJsonAsStringFrom(connection);
            Boolean existence=articleExistenceChecker(jsonData);
            if (existence) {
                outputText.appendText("Article exists.\n");
            } else {
                outputText.appendText("Article does not exist.\n");
            }

            String redirectTarget = redirectChecker(jsonData);
            if (redirectTarget != null) {
                outputText.appendText("Redirected to: " + redirectTarget + "\n");
            }
            printRecentChanges(jsonData);
        } catch (IOException e) {
            NetworkErrorHandler(e);
        }
    }


    public static String redirectChecker(String jsonData) {
        JsonObject CheckObject = JsonParser.parseString(jsonData).getAsJsonObject();
        if (CheckObject.has("query")) {
            JsonObject jsonQueryObject = CheckObject.getAsJsonObject("query");
            if (jsonQueryObject.has("redirects")) {
                JsonArray redirectArray = jsonQueryObject.getAsJsonArray("redirects");
                if (redirectArray.size() > 0) {
                    JsonObject recentRedirects = redirectArray.get(0).getAsJsonObject();
                    return recentRedirects.get("to").getAsString();
                }
            }
        }
        return null;
    }

    public static void NetworkErrorHandler(IOException e) {
        System.err.println("Error: Network Error has been Detected!");
        e.printStackTrace();
        System.exit(1);
    }

    public static void MissingNameHandler() {
        System.err.println("Error: Article Name Not Given!");
        System.exit(1);
    }

    public static boolean articleExistenceChecker(String jsonData) {
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

