package edu.bsu.cs222;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.*;

public class PullfromWiki {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter Article Name(Case-Specific): ");
        String articleTitle = scanner.nextLine().trim();
        if (articleTitle.isEmpty()) {
            MissingNameHandler();
        }
        try {
            URLConnection connection = connectToWikipedia(articleTitle);
            String jsonData = readJsonAsStringFrom(connection);
            String redirectedArticle = redirectChecker(jsonData);
            if (redirectedArticle != null) {
                System.out.println("Redirected to: " + redirectedArticle);
            }
            if (articleExistenceCheck(jsonData)) {
                printChanges(jsonData);
            } else {
                System.out.println("Article not found: " + articleTitle);
            }
            printRawJson(jsonData);
            connection.getInputStream().close();
            System.exit(0);
        } catch (IOException e) {
            NetworkErrorHandler(e);
        }
        scanner.close();
    }

    private static String redirectChecker(String jsonData) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        if (jsonObject.has("query")) {
            JsonObject query = jsonObject.getAsJsonObject("query");
            if (query.has("redirects")) {
                JsonArray redirects = query.getAsJsonArray("redirects");
                if (redirects.size() > 0) {
                    JsonObject firstRedirect = redirects.get(0).getAsJsonObject();
                    return firstRedirect.get("to").getAsString();
                }
            }
        }
        return null;
    }

    private static void NetworkErrorHandler(IOException e) {
        System.err.println("Error: Network Error has Occurred!");
        e.printStackTrace();
        System.exit(1);
    }

    private static void MissingNameHandler() {
        System.err.println("Please Provide Article Name!");
        System.exit(1);
    }

    public static boolean articleExistenceCheck(String jsonData) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();
        if (jsonObject.has("query")) {
            JsonObject query = jsonObject.getAsJsonObject("query");
            if (query.has("pages")) {
                JsonObject pages = query.getAsJsonObject("pages");
                for (String pageId : pages.keySet()) {
                    JsonObject page = pages.getAsJsonObject(pageId);
                    return !page.has("missing");
                }
            }
        }
        return false;
    }


    public static void printChanges(String jsonData) {
        JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

        if (jsonObject.has("query")) {
            JsonObject query = jsonObject.getAsJsonObject("query");

            if (query.has("pages")) {
                JsonObject pages = query.getAsJsonObject("pages");

                for (String pageId : pages.keySet()) {
                    JsonObject page = pages.getAsJsonObject(pageId);

                    if (page.has("revisions")) {
                        JsonArray revisions = page.getAsJsonArray("revisions");

                        if (revisions.size() == 0) {
                            System.out.println("No recent changes found for the article: " + page.get("title"));
                        } else {
                            System.out.println("Recent changes for the article: " + page.get("title"));
                            List<JsonObject> revisionList = new ArrayList<>();
                            for (JsonElement revisionElement : revisions) {
                                revisionList.add(revisionElement.getAsJsonObject());
                            }
                            revisionList.sort(Comparator.comparing(
                                    revision -> revision.get("timestamp").getAsString(),
                                    Comparator.reverseOrder()
                            ));
                            for (JsonObject changeObject : revisionList) {
                                String timestamp = changeObject.get("timestamp").getAsString();
                                String user = changeObject.get("user").getAsString();

                                System.out.println(timestamp + " " + user + "\n");
                            }
                        }
                    } else {
                        System.out.println("No recent changes found for the article: " + page.get("title"));
                    }
                }
            } else {
                System.out.println("No query data found.");
            }
        } else {
            System.out.println("No query data found.");
        }
    }


    public static URLConnection connectToWikipedia(String articleTitle) throws IOException {
        String UrlString = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=revisions&titles=" +
                articleTitle +
                "&rvprop=timestamp|user&rvlimit=13&redirects";
        URL url = new URL(UrlString);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent",
                "CS222FirstProject/0.1 (devlin.hicks@bsu.edu)");
        connection.connect();
        return connection;
    }

    public static String readJsonAsStringFrom(URLConnection connection) throws IOException {
        try (InputStream inputStream = connection.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
            StringBuilder responseBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            int BytesRead;
            while ((BytesRead = reader.read(buffer)) != -1) {
                responseBuilder.append(buffer, 0, BytesRead);
            }
            return responseBuilder.toString();
        }
    }

    public static void printRawJson(String jsonData) {
        System.out.println(jsonData + "\n");
    }


    public static String readFileAsString(String s) throws IOException {
        try {
            InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream("scratch.json");
            return new String(Objects.requireNonNull(file).readAllBytes(), Charset.defaultCharset());
        } catch (IOException e) {
            MissingNameHandler();
        }

        return s;
    }
}




