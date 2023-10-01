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
public class wikipediaLoader {


    public static void main(String[] args) {
        Scanner terminal = new Scanner(System.in);
        System.out.println("Type in your Article Title Right Here: ");
        String wikiTitle = terminal.nextLine();
        if (wikiTitle.isBlank()) {
            MissingNameHandler();
        }
        try {
            URLConnection wikiConnection = connectToWikipedia(wikiTitle);
            String jsonData = readJsonAsStringFrom(wikiConnection);
            String articleRedirect = redirectChecker(jsonData);
            if (articleRedirect != null) {
                System.out.println("You will be redirected to " + articleRedirect);
            }
            if (articleExistenceChecker(jsonData)) {
                printChanges(jsonData);
            } else {
                System.out.println(wikiTitle + " does not exist.");
            }
            System.out.println("\n");
            printRawJson(jsonData);
            wikiConnection.getInputStream().close();
            System.exit(0);
        } catch (IOException e) {
            NetworkErrorHandler(e);
        }
        terminal.close();
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


    public static void printChanges(String jsonData) {
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
                            System.out.println("No recent changes were found in: " + page.get("title"));
                        } else {
                            System.out.println("Recent changes found for: " + page.get("title"));
                            List<JsonObject> revisionArrayList = new ArrayList<>();
                            for (JsonElement revisionElement : revisions) {
                                revisionArrayList.add(revisionElement.getAsJsonObject());
                            }
                            revisionArrayList.sort(Comparator.comparing(
                                    revision -> revision.get("timestamp").getAsString(),
                                    Comparator.reverseOrder()
                            ));
                            for (JsonObject changeObject : revisionArrayList) {
                                String time = changeObject.get("timestamp").getAsString();
                                String users = changeObject.get("user").getAsString();

                                System.out.println("Time: " + time);
                                System.out.println("User: " + users + "\n");
                            }
                        }
                    } else {
                        System.out.println("No recent changes found in " + page.get("title"));
                    }
                }
            } else {
                System.out.println("Query Data Not Found!");
            }
        } else {
            System.out.println("Query Data Not Found!");
        }
    }


    public static URLConnection connectToWikipedia(String articleTitle) throws IOException {
        String UrlString = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=revisions&titles=" +
                articleTitle +
                "&rvprop=timestamp|user&rvlimit=13&redirects";
        URL site = new URL(UrlString);
        URLConnection connection = site.openConnection();
        connection.setRequestProperty("User-Agent",
                "CS222FirstProject/0.1 (devlin.hicks@bsu.edu)");
        connection.connect();
        return connection;
    }

    public static String readJsonAsStringFrom(URLConnection connection) throws IOException {
        try (InputStream dataStream = connection.getInputStream()) {
            InputStreamReader streamReader = new InputStreamReader(dataStream, Charset.defaultCharset());
            StringBuilder convertedString = new StringBuilder();
            char[] stringBuffer = new char[1024];
            int AllBytesRead;
            while ((AllBytesRead = streamReader.read(stringBuffer)) != -1) {
                convertedString.append(stringBuffer, 0, AllBytesRead);
            }
            return convertedString.toString();
        }
    }

    public static void printRawJson(String jsonData) {
        System.out.println(jsonData + "\n");
    }


    public static String readFileAsString(String s) {
        try {
            InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream("scratch.json");
            return new String(Objects.requireNonNull(file).readAllBytes(), Charset.defaultCharset());
        } catch (IOException e) {
            MissingNameHandler();
        }
        return s;
    }
}







