package edu.bsu.cs222;

import java.io.IOException;
import java.net.URLConnection;

import static edu.bsu.cs222.PullfromWiki.printRawJson;
import static edu.bsu.cs222.PullfromWiki.readJsonAsStringFrom;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class tests {
    public void TestWikiConnection() throws IOException{
        URLConnection connection=PullfromWiki.connectToWikipedia();
        assertNotNull(connection);
    }
    public void TestJsonStream() throws IOException{
        URLConnection connect=PullfromWiki.connectToWikipedia();
        readJsonAsStringFrom(connect);
        printRawJson(String.valueOf(connect));
    }


}
