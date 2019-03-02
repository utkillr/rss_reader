package model;

import config.RSSConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import util.PubDateParser;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RSSItemTest {

    @Before
    public void setUp() {
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
        RSSConfiguration.getInstance().addRSSFeed("dummy.rss", "dummy.txt");
    }

    @After
    public void tearDown() {
        try {
            RSSConfiguration.getInstance().delRSSFeed("dummy.rss");
        } catch (Exception ignored) {}
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
    }

    @Test(expected = InvalidObjectException.class)
    @DisplayName("Test unconfigured RSSItem invalid initialization")
    public void initRSSItemUnconfigured() throws InvalidObjectException {
        tearDown();
        Map<String, String> source = new HashMap<>();
        RSSConfiguration.getRawMandatoryItemFields().forEach(field -> source.put(field, "dummy " + field));
        source.put("pubdate", "Tue, 03 May 2016 11:46:11 +0200");
        new RSSItem(RSSConfiguration.getInstance(), "dummy.rss", source);
    }

    @Test(expected = InvalidObjectException.class)
    @DisplayName("Test RSSItem invalid initialization")
    public void initRSSItemInvalid() throws InvalidObjectException {
        Map<String, String> source = new HashMap<>();
        source.put("description", "dummy description");
        source.put("title", "dummy title");
        new RSSItem(RSSConfiguration.getInstance(), "dummy.rss", source);
    }

    @Test(expected = InvalidObjectException.class)
    @DisplayName("Test invalid Latest PubDate logic")
    public void latestPubDateTestInvalid() throws InvalidObjectException {
        Map<String, String> source = new HashMap<>();
        RSSConfiguration.getRawMandatoryItemFields().forEach(field -> source.put(field, "dummy " + field));
        source.put("pubdate", null);
        new RSSItem(RSSConfiguration.getInstance(), "dummy.rss", source);
    }

    @Test
    @DisplayName("Test RSSItem Latest PubDate logic")
    public void latestPubDateTest() throws InvalidObjectException {
        Map<String, String> source = new HashMap<>();
        RSSConfiguration.getRawMandatoryItemFields().forEach(field -> source.put(field, "dummy " + field));
        source.put("pubdate", "Tue, 03 May 2016 11:46:11 +0200");
        RSSItem item = new RSSItem(RSSConfiguration.getInstance(), "dummy.rss", source);
        assertEquals(PubDateParser.parse("Tue, 03 May 2016 11:46:11 +0200"), item.getLatestPubDate());
    }

    @Test
    @DisplayName("Test RSSItem body")
    public void bodyTest() throws InvalidObjectException {
        Map<String, String> source = new HashMap<>();
        RSSConfiguration.getRawMandatoryItemFields().forEach(field -> source.put(field.toLowerCase(), "dummy " + field.toLowerCase()));
        source.put("pubdate", "Tue, 03 May 2016 11:46:11 +0200");
        RSSConfiguration.getInstance().reconfig("dummy.rss", new ArrayList<>(source.keySet()), null);

        RSSItem item = new RSSItem(RSSConfiguration.getInstance(), "dummy.rss", source);
        assertEquals(PubDateParser.parse("Tue, 03 May 2016 11:46:11 +0200"), item.getLatestPubDate());
        RSSConfiguration.getInstance().getItemFields("dummy.rss")
                .stream()
                .filter(field -> !field.equals("pubdate"))
                .forEach(field -> assertEquals("dummy " + field, item.getBody().get(field)));
    }
}
