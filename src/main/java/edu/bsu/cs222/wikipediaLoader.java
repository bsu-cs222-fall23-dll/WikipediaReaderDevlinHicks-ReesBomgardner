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
                System.out.println("Redirect to: " + redirectedArticle);
            }
            if (articleExistenceCheck(jsonData)) {
                printChanges(jsonData);
            } else {
                System.out.println(articleTitle + " not found!");
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
        JsonObject Object = JsonParser.parseString(jsonData).getAsJsonObject();
        if (Object.has("query")) {
            JsonObject queryObject = Object.getAsJsonObject("query");
            if (queryObject.has("redirects")) {
                JsonArray redirectTotal = queryObject.getAsJsonArray("redirects");
                if (redirectTotal.size() > 0) {
                    JsonObject redirects = redirectTotal.get(0).getAsJsonObject();
                    return redirects.get("to").getAsString();
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
        System.err.println("Error: Article Name Not Given!");
        System.exit(0);
    }

    public static boolean articleExistenceCheck(String jsonData) {
        JsonObject jsonArticle = JsonParser.parseString(jsonData).getAsJsonObject();
        if (jsonArticle.has("query")) {
            JsonObject query = jsonArticle.getAsJsonObject("query");
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
                            System.out.println("No recent changes found in: " + page.get("title"));
                        } else {
                            System.out.println("Recent changes for: " + page.get("title"));
                            List<JsonObject> revisionsList = new ArrayList<>();
                            for (JsonElement revisionElement : revisions) {
                                revisionsList.add(revisionElement.getAsJsonObject());
                            }
                            revisionsList.sort(Comparator.comparing(
                                    revision -> revision.get("timestamp").getAsString(),
                                    Comparator.reverseOrder()
                            ));
                            for (JsonObject changeObject : revisionsList) {
                                String time = changeObject.get("timestamp").getAsString();
                                String users = changeObject.get("user").getAsString();


                                System.out.println(time + " " + users + "\n");
                            }
                        }
                    } else {
                        System.out.println("No recent changes found for the article: " + page.get("title"));
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
        try (InputStream inputStream = connection.getInputStream()) {
            InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.defaultCharset());
            StringBuilder responseStringBuilder = new StringBuilder();
            char[] buffer = new char[1024];
            int AllBytesRead;
            while ((AllBytesRead = streamReader.read(buffer)) != -1) {
                responseStringBuilder.append(buffer, 0, AllBytesRead);
            }
            return responseStringBuilder.toString();
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




