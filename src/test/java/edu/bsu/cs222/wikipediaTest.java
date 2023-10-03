package edu.bsu.cs222;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URLConnection;

public class wikipediaTest {
    @Test
    public void TestWikiConnection() throws IOException{
        String name="Frank Zappa";
        URLConnection testConnect = wikipediaLoader.connectToWikipedia(name);
        Assertions.assertNotNull(testConnect);
    }
    @Test
    public void TestFileRead() {
        String fileData = wikipediaLoader.readFileAsString("scratch.json");
        Assertions.assertNotNull(fileData);
    }
    @Test
    public JsonArray getRevisions(String JsonData){
        JsonObject revisionObject = JsonParser.parseString(JsonData).getAsJsonObject();
        JsonObject queries= revisionObject.getAsJsonObject("query");
        JsonObject pages=queries.getAsJsonObject("pages");
        JsonObject page = pages.entrySet().iterator().next().getValue().getAsJsonObject();
        return page.getAsJsonArray("revisions");

    }

    @Test
    public void testRevisionCount() {
        String revisionTestData = wikipediaLoader.readFileAsString("scratch.json");
        JsonArray revisions=getRevisions(revisionTestData);
        Assertions.assertEquals(4, revisions.size());

    }
    @Test
    public void testGUI(){

    }


}
