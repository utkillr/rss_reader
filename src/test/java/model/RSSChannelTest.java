package model;

import config.RSSConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.rules.ExpectedException;
import util.PubDateParser;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RSSChannelTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

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

    @Test
    @DisplayName("Test unconfigured RSSChannel invalid initialization")
    public void initRSSChannelUnconfiguredTest() throws InvalidObjectException {
        exception.expect(InvalidObjectException.class);
        exception.expectMessage("RSS Configuration");

        tearDown();
        FeedModel model = new FeedModel();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> model.metaSource.put(field, "dummy " + field));
        Map<String, String> itemSource = new HashMap<>();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> itemSource.put(field, "dummy " + field));
        itemSource.put("pubdate", "Tue, 03 May 2016 11:46:11 +0200");
        model.itemSources.add(itemSource);
        new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
    }

    @Test
    @DisplayName("Test RSSChannel with invalid RSSItems initialization")
    public void initRSSChannelInvalidRSSItemTest() throws InvalidObjectException {
        exception.expect(InvalidObjectException.class);
        exception.expectMessage("RSS Item");

        FeedModel model = new FeedModel();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> model.metaSource.put(field, "dummy " + field));
        Map<String, String> itemSource = new HashMap<>();
        itemSource.put("description", "dummy description");
        itemSource.put("title", "dummy title");
        model.itemSources.add(itemSource);
        new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
    }

    @Test
    @DisplayName("Test RSSChannel invalid initialization")
    public void initRSSChannelInvalid() throws InvalidObjectException {
        exception.expect(InvalidObjectException.class);
        exception.expectMessage("RSS Channel");

        FeedModel model = new FeedModel();
        model.metaSource.put("description", "dummy description");
        Map<String, String> itemSource = new HashMap<>();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> itemSource.put(field, "dummy " + field));
        itemSource.put("pubdate", "Tue, 03 May 2016 11:46:11 +0200");
        model.itemSources.add(itemSource);
        new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
    }

    @Test
    @DisplayName("Test RSSChannel Latest PubDate logic")
    public void latestPubDateTest() throws InvalidObjectException {
        FeedModel model = new FeedModel();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> model.metaSource.put(field, "dummy " + field));

        Map<String, String> itemSource1 = new HashMap<>();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> itemSource1.put(field, "dummy " + field));
        itemSource1.put("pubdate", "Tue, 03 May 2016 11:46:11 +0200");

        Map<String, String> itemSource2 = new HashMap<>();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> itemSource2.put(field, "newdummy " + field));
        itemSource2.put("pubdate", "Tue, 04 May 2016 11:46:11 +0200");

        model.itemSources.add(itemSource1);

        RSSChannel channel1 = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        assertEquals(PubDateParser.parse("Tue, 03 May 2016 11:46:11 +0200"), channel1.getLatestPubDate());

        model.itemSources.add(itemSource2);

        RSSChannel channel2 = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        assertEquals(PubDateParser.parse("Tue, 04 May 2016 11:46:11 +0200"), channel2.getLatestPubDate());
    }

    @Test
    @DisplayName("Test RSSChannel metaBody")
    public void metaBodyTest() throws InvalidObjectException {
        FeedModel model = new FeedModel();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> model.metaSource.put(field, "dummy " + field));

        Map<String, String> itemSource = new HashMap<>();
        RSSConfiguration.getRawMandatoryItemFields().forEach(field -> itemSource.put(field, "dummy " + field));
        itemSource.put("pubdate", "Tue, 03 May 2016 11:46:11 +0200");
        model.itemSources.add(itemSource);

        RSSConfiguration.getInstance().reconfig("dummy.rss", null, new ArrayList<>(model.metaSource.keySet()));

        RSSChannel channel = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        assertEquals(PubDateParser.parse("Tue, 03 May 2016 11:46:11 +0200"), channel.getLatestPubDate());
        RSSConfiguration.getInstance().getChannelFields("dummy.rss")
                .stream()
                .filter(field -> !field.equals("pubdate"))
                .forEach(field -> assertEquals("dummy " + field, channel.getMetaBody().get(field)));
    }

    @Test
    @DisplayName("Test RSSChannel throws when there is no new items")
    public void allItemsOlderThanLatestPubDateTest() throws InvalidObjectException {
        FeedModel model = new FeedModel();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> model.metaSource.put(field, "dummy " + field));

        Map<String, String> itemSource1 = new HashMap<>();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> itemSource1.put(field, "dummy " + field));
        itemSource1.put("pubdate", "Tue, 03 May 2016 11:46:11 +0200");

        model.itemSources.add(itemSource1);
        RSSConfiguration.getInstance().notifyFeedRead("dummy.rss", PubDateParser.parse("Tue, 04 May 2016 11:46:11 +0200"));

        RSSChannel channel = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        assertEquals(0, channel.getItems().size());
    }

    @Test
    @DisplayName("Test RSSChannel limiting RSS Items")
    public void initRSSChannelTooManyItems() throws InvalidObjectException {
        FeedModel model = new FeedModel();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> model.metaSource.put(field, "dummy " + field));

        Map<String, String> itemSource1 = new HashMap<>();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> itemSource1.put(field, "dummy " + field));
        itemSource1.put("pubdate", "Tue, 05 May 2016 11:46:11 +0200");

        Map<String, String> itemSource2 = new HashMap<>();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> itemSource2.put(field, "dummy " + field));
        itemSource2.put("pubdate", "Tue, 03 May 2016 11:46:11 +0200");

        Map<String, String> itemSource3 = new HashMap<>();
        RSSConfiguration.getRawMandatoryChannelFields().forEach(field -> itemSource3.put(field, "dummy " + field));
        itemSource3.put("pubdate", "Tue, 04 May 2016 11:46:11 +0200");

        model.itemSources.add(itemSource1);
        model.itemSources.add(itemSource2);
        model.itemSources.add(itemSource3);

        RSSConfiguration.getInstance().setFeedMaxItems("dummy.rss", 2);
        RSSChannel channel = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        assertEquals(2, channel.getItems().size());
        for (RSSItem item : channel.getItems()) {
            assertNotEquals(PubDateParser.parse("Tue, 03 May 2016 11:46:11 +0200"), item.getLatestPubDate());
        }
    }
}
