package edu.bsu.cs222;

import java.io.IOException;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class tests {
    public void TestWikiConnection() throws IOException{
        URLConnection connection=PullfromWiki.connectToWikipedia();
        assertNotNull(connection);
    }
    public void TestJsonStream() throws IOException{

    }


}
