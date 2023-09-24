package edu.bsu.cs222;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Objects;
import org.json.simple.JSONArray;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class tests {
    @Test
    public void TestWikiConnection() throws IOException{
        URLConnection connection=PullfromWiki.connectToWikipedia();
        assertNotNull(connection);
    }
    @Test
    public void TestJsonAccess() throws IOException{
        String JsonData=readFileasString();
        Assertions.assertNotNull(JsonData);
    }

    private String readFileasString() throws IOException,NullPointerException {
        InputStream file=Thread.currentThread().getContextClassLoader().getResourceAsStream("scratch.json");
        return new String(Objects.requireNonNull(file).readAllBytes(), Charset.defaultCharset());
    }
    private JSONArray getRevisions(String JsonData){
        return JsonData.read(JsonData,"$...revisions[*]");

    }
    public void testRevisionCount() throws IOException{
        String JsonData=readFileasString();
        JSONArray revisions=getRevisions(JsonData);
        Assertions.assertEquals(4, revisions.size());

    }


}
