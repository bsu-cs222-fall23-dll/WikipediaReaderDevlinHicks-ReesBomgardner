package edu.bsu.cs222;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class PullfromWiki {
    public static void main(String[] args) throws IOException {
        URLConnection connection = connectToWikipedia();
        String jsonData = readJsonAsStringFrom(connection);
        printRawJson(jsonData);
    }

    public static URLConnection connectToWikipedia() throws IOException {
        String encodedUrlString = "https://en.wikipedia.org/w/api.php?action=query&format=json&prop=revisions&titles=" +
                URLEncoder.encode("Zappa", Charset.defaultCharset()) +
                "&rvprop=timestamp|user&rvlimit=4&redirects";
        URL url = new URL(encodedUrlString);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent",
                "CS222FirstProject/0.1 (dllargent@bsu.edu)");
        connection.connect();
        return connection;
    }

    private static String readJsonAsStringFrom(URLConnection connection) throws IOException {
        return new String(connection.getInputStream().readAllBytes(), Charset.defaultCharset());
    }

    private static void printRawJson(String jsonData) {
        System.out.println(jsonData);
    }

}

