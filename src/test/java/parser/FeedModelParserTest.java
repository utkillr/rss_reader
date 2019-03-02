package parser;

import model.FeedModel;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeedModelParserTest {

    private FeedModel getRegularFeedModel() {
        FeedModel model = new FeedModel();
        model.metaSource.put("title", "CHANNEL NAME");
        model.metaSource.put("description", "CHANNEL DESCRIPTION");
        Map<String, String> source = new HashMap<>();
        source.put("title", "NAME");
        source.put("description", "DESCRIPTION");
        source.put("pubdate", "DATE");
        model.itemSources.add(source);
        return model;
    }

    private void assertModelIs(FeedModel receivedModel, FeedModel expectedModel) {
        assertEquals(expectedModel.metaSource.size(), receivedModel.metaSource.size());
        assertEquals(1, expectedModel.itemSources.size());
        assertEquals(1, receivedModel.itemSources.size());
        for (String key : expectedModel.metaSource.keySet()) {
            assertEquals(expectedModel.metaSource.get(key), receivedModel.metaSource.get(key));
        }
        for (String key : expectedModel.itemSources.get(0).keySet()) {
            assertEquals(expectedModel.itemSources.get(0).get(key), receivedModel.itemSources.get(0).get(key));
        }
    }

    @Test
    @DisplayName("Test for regular RSS Feed parsing")
    public void parseValidRSSFeedTest() {
        String file = "parser" + File.separator + "regularRss.xml";
        InputStream inputStream = FeedModelParser.class.getClassLoader().getResourceAsStream(file);

        FeedModel model = new FeedModelParser().parse(inputStream);
        assertModelIs(model, getRegularFeedModel());
    }

    @Test
    @DisplayName("Test for ability to parse RSSFeed with sub tag")
    public void parseRSSFeedWithSubTagTest() {
        String file = "parser" + File.separator + "rssWithSubTag.xml";
        InputStream inputStream = FeedModelParser.class.getClassLoader().getResourceAsStream(file);

        FeedModel model = new FeedModelParser().parse(inputStream);
        assertModelIs(model, getRegularFeedModel());
    }

    @Test
    @DisplayName("Test for ability to parse RSSFeed with sub rss")
    public void parseRSSFeedWithSubItemTest() {
        String file = "parser" + File.separator + "rssWithSubRss.xml";
        InputStream inputStream = FeedModelParser.class.getClassLoader().getResourceAsStream(file);

        FeedModel model = new FeedModelParser().parse(inputStream);
        assertModelIs(model, getRegularFeedModel());
    }

    @Test
    @DisplayName("Test for Empty RSSFeed parsing")
    public void parseEmptyRSSFeedTest() {
        String file = "parser" + File.separator + "emptyRss.xml";
        InputStream inputStream = FeedModelParser.class.getClassLoader().getResourceAsStream(file);

        FeedModel model = new FeedModelParser().parse(inputStream);
        assertTrue(model.metaSource.isEmpty());
        assertTrue(model.itemSources.isEmpty());
    }

    @Test
    @DisplayName("Test for RSSFeed parsing with noise")
    public void parseRSSFeedTestWithNoise() {
        String file = "parser" + File.separator + "rssWithNoise.xml";
        InputStream inputStream = FeedModelParser.class.getClassLoader().getResourceAsStream(file);

        FeedModel model = new FeedModelParser().parse(inputStream);
        assertModelIs(model, getRegularFeedModel());
    }

    @Test
    @DisplayName("Test for RSSFeed parsing with multiple channels")
    public void parseRSSFeedTestWithMultipleChannels() {
        String file = "parser" + File.separator + "rssWithMultipleChannels.xml";
        InputStream inputStream = FeedModelParser.class.getClassLoader().getResourceAsStream(file);

        FeedModel model = new FeedModelParser().parse(inputStream);
        assertModelIs(model, getRegularFeedModel());
    }

    @Test
    @DisplayName("Test for Deep lying RSSFeed")
    public void parseDeepRSSFeedTest() {
        String file = "parser" + File.separator + "rssDeep.xml";
        InputStream inputStream = FeedModelParser.class.getClassLoader().getResourceAsStream(file);

        FeedModel model = new FeedModelParser().parse(inputStream);
        assertModelIs(model, getRegularFeedModel());
    }

    @Test
    @DisplayName("RSS Feed Test for invalid XML")
    public void RSSFeedInvalidXMLTest() {
        String file = "parser" + File.separator + "invalid.xml";
        InputStream inputStream = FeedModelParser.class.getClassLoader().getResourceAsStream(file);
        FeedModel model = new FeedModelParser().parse(inputStream);

        assertTrue(model.metaSource.isEmpty());
        assertTrue(model.itemSources.isEmpty());
    }

    @Test
    @DisplayName("Test for Atom RSS Feed parsing")
    public void parseAtomRSSFeedTest() {
        String file = "parser" + File.separator + "atomRss.xml";
        InputStream inputStream = FeedModelParser.class.getClassLoader().getResourceAsStream(file);

        FeedModel expectedModel = new FeedModel();
        expectedModel.metaSource.put("atom:title", "CHANNEL NAME");
        expectedModel.metaSource.put("atom:summary", "CHANNEL DESCRIPTION");
        Map<String, String> source = new HashMap<>();
        source.put("atom:title", "NAME");
        source.put("atom:content", "DESCRIPTION");
        source.put("atom:published", "DATE");
        expectedModel.itemSources.add(source);

        FeedModel model = new FeedModelParser().parse(inputStream);
        assertModelIs(model, expectedModel);
    }
}
