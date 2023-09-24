package edu.bsu.cs222;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Objects;

public class PullfromWiki {

    public static void main(String[] args) throws IOException {
        if (args.length==0){
            System.err.println("Error: Article Name not Given!");
            System.exit(1);
        }
        String articleTitle =args[0];
        URLConnection connection = connectToWikipedia(articleTitle);
        String jsonData = readJsonAsStringFrom(connection);
        printRawJson(jsonData);
        PrintChanges(jsonData, articleTitle);
    }

    private static void PrintChanges(String jsonData, String articleName) {
        JsonObject Response = JsonParser.parseString(jsonData).getAsJsonObject();
        JsonObject query=Response.getAsJsonObject("query");
        JsonObject page=query.getAsJsonObject();
        if(page.has("-1")){
            System.err.println("Error: Wikipedia Article Not Found! ");
            System.exit(0);
        } else {
            JsonObject Data = page.entrySet().iterator().next().getValue().getAsJsonObject();
            if(Data.has("title")) {
                String PageTitle=Data.get("title").getAsString();
                if(!PageTitle.equals(articleName)){
                    System.out.println("Redirect to: "+PageTitle);
                }
            }
            JsonArray TotalRevisions=Data.getAsJsonArray("revisions");
            for(int i=0;i<TotalRevisions.size();i++){
                JsonObject revision=TotalRevisions.get(i).getAsJsonObject();
                String timestamp=revision.get("timestamp").getAsString();
                String user=revision.get("user").getAsString();
                System.out.println(timestamp+" "+ user);
            }

        }

    }

    public static URLConnection connectToWikipedia(String ArticleName) throws IOException {
        String UrlString = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=revisions&titles=" +
                URLEncoder.encode("Zappa", Charset.defaultCharset()) +
                "&rvprop=timestamp|user&rvlimit=4&redirects";
        URL url = new URL(UrlString);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent",
                "CS222FirstProject/0.1 (dllargent@bsu.edu)");
        connection.connect();
        return connection;
    }

    public static String readJsonAsStringFrom(URLConnection connection) throws IOException {
        return new String(connection.getInputStream().readAllBytes(), Charset.defaultCharset());
    }

    public static void printRawJson(String jsonData) {
        System.out.println(jsonData);
    }

    public static String readFileAsString(String s) throws IOException {
        InputStream file=Thread.currentThread().getContextClassLoader().getResourceAsStream("scratch.json");
        return new String(Objects.requireNonNull(file).readAllBytes(), Charset.defaultCharset());
    }


}

