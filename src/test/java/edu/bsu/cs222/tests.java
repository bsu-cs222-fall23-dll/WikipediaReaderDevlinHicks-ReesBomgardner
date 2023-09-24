package edu.bsu.cs222;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class tests {
    @Test
    public void TestWikiConnection() throws IOException{
        URLConnection connection=PullfromWiki.connectToWikipedia();
        assertNotNull(connection);
    }
    @Test
    public void TestJsonStream() throws IOException{
        URL link=new URL("https://en.wikipedia.org/wiki/Led_Zeppelin");
        URLConnection connect=link.openConnection();
        String JsonData=PullfromWiki.readJsonAsStringFrom(connect);
        assertNotNull(connect);
        PullfromWiki.printRawJson(JsonData);
    }


}
