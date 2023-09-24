package edu.bsu.cs222;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLConnection;

public class tests {
    @Test
    public void TestWikiConnection() throws IOException{
        String name="Frank Zappa";
        URLConnection connect = PullfromWiki.connectToWikipedia(name);
        Assertions.assertNotNull(connect);
    }
    @Test
    public void TestFileRead() throws IOException{
        String jsonData = PullfromWiki.readFileAsString("scratch.json");
        Assertions.assertNotNull(jsonData);
    }
    @Test
    public JsonArray getRevisions(String JsonData){
        JsonObject Object = JsonParser.parseString(JsonData).getAsJsonObject();
        JsonObject query= Object.getAsJsonObject("query");
        JsonObject pages=query.getAsJsonObject("pages");
        JsonObject page = pages.entrySet().iterator().next().getValue().getAsJsonObject();
        return page.getAsJsonArray("revisions");

    }

    @Test
    public void testRevisionCount() throws IOException{
        String jsonData = PullfromWiki.readFileAsString("scratch.json");
        JsonArray revisions=getRevisions(jsonData);
        Assertions.assertEquals(4, revisions.size());

    }


}
