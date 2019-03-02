package config;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.nio.file.StandardOpenOption.APPEND;
import static org.junit.Assert.*;

public class AutoRSSConfiguratorTest {

    private String file = "test.cfg";

    public void cleanup() {
        List<String> feeds = new ArrayList<>(RSSConfiguration.getInstance().getRSSFeeds().keySet());
        feeds.forEach(RSSConfiguration.getInstance()::delRSSFeed);
        RSSConfiguration.getInstance().setTimeToPoll(RSSConfiguration.defaultTimeToPoll);
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
    }

    public void deleteFile(String file) {
        File f = new File(file);
        f.delete();
    }

    public void streamToFile(String file) throws IOException {
        File f = new File(this.file);
        f.delete();
        f.createNewFile();
        InputStream in = RSSConfigurationTest.class.getClassLoader().getResourceAsStream("config" + File.separator + file);
        byte[] bytes = new byte[in.available()];
        in.read(bytes);
        Files.write(f.toPath(), bytes, APPEND);
    }

    @Test
    @DisplayName("Test of ability to save and load configurations")
    public void autoReconfigValidTest() throws IOException {
        cleanup();
        deleteFile(file);

        AutoRSSConfigurator.setFile(file);

        RSSConfiguration.getInstance().setTimeToPoll(100L);

        List<String> dummyChannelFields = Arrays.asList("description", "title", "link", "copyright");
        List<String> dummyItemFields = Arrays.asList("description", "title", "link", "pubdate");
        RSSConfiguration.getInstance().addRSSFeed("dummy.rss", "dummy.txt");
        RSSConfiguration.getInstance().turnOffRSSFeed("dummy.rss");
        RSSConfiguration.getInstance().notifyFeedRead("dummy.rss", null);
        RSSConfiguration.getInstance().reconfig("dummy.rss", dummyItemFields, dummyChannelFields);

        List<String> newdummyChannelFields = Arrays.asList("description", "title");
        List<String> newdummyItemFields = Arrays.asList("title", "link");
        Date newdummyDate = new Date(1000000L);
        RSSConfiguration.getInstance().addRSSFeed("newdummy.rss", "newdummy.txt");
        RSSConfiguration.getInstance().turnOnRSSFeed("newdummy.rss");
        RSSConfiguration.getInstance().notifyFeedRead("newdummy.rss", newdummyDate);
        RSSConfiguration.getInstance().reconfig("newdummy.rss", newdummyItemFields, newdummyChannelFields);

        AutoRSSConfigurator.saveRSSConfiguration();

        cleanup();

        AutoRSSConfigurator.loadRSSConfiguration();

        assertEquals((Long)100L, RSSConfiguration.getInstance().getTimeToPoll());

        assertEquals("dummy.txt", RSSConfiguration.getInstance().getRSSFeeds().get("dummy.rss"));
        assertFalse(RSSConfiguration.getInstance().isRSSFeedOn("dummy.rss"));
        assertNull(RSSConfiguration.getInstance().getRSSFeedLastPubDate("dummy.rss"));
        assertTrue(RSSConfiguration.getInstance().getChannelFields("dummy.rss").containsAll(dummyChannelFields));
        assertTrue(dummyChannelFields.containsAll(RSSConfiguration.getInstance().getChannelFields("dummy.rss")));
        assertTrue(RSSConfiguration.getInstance().getItemFields("dummy.rss").containsAll(dummyItemFields));
        assertTrue(dummyItemFields.containsAll(RSSConfiguration.getInstance().getItemFields("dummy.rss")));

        assertEquals("newdummy.txt", RSSConfiguration.getInstance().getRSSFeeds().get("newdummy.rss"));
        assertTrue(RSSConfiguration.getInstance().isRSSFeedOn("newdummy.rss"));
        assertEquals(newdummyDate, RSSConfiguration.getInstance().getRSSFeedLastPubDate("newdummy.rss"));
        assertTrue(RSSConfiguration.getInstance().getChannelFields("newdummy.rss").containsAll(newdummyChannelFields));
        assertTrue(newdummyChannelFields.containsAll(RSSConfiguration.getInstance().getChannelFields("newdummy.rss")));
        assertTrue(RSSConfiguration.getInstance().getItemFields("newdummy.rss").containsAll(newdummyItemFields));
        assertTrue(newdummyItemFields.containsAll(RSSConfiguration.getInstance().getItemFields("newdummy.rss")));

        cleanup();
        deleteFile(file);
    }

    @Test
    @DisplayName("Test of ability to load configurations")
    public void autoConfigValidTest() throws IOException {
        streamToFile("validConfig.cfg");

        AutoRSSConfigurator.setFile(file);

        List<String> dummyChannelFields = Arrays.asList("description", "title");
        List<String> dummyItemFields = Arrays.asList("description", "title", "pubdate");
        Date dummyDate = new Date(1000000L);

        AutoRSSConfigurator.loadRSSConfiguration();

        assertEquals((Long)300L, RSSConfiguration.getInstance().getTimeToPoll());
        assertEquals(1, RSSConfiguration.getInstance().getRSSFeeds().size());

        assertEquals("dummy.txt", RSSConfiguration.getInstance().getRSSFeeds().get("dummy.rss"));
        assertFalse(RSSConfiguration.getInstance().isRSSFeedOn("dummy.rss"));
        assertEquals(dummyDate, RSSConfiguration.getInstance().getRSSFeedLastPubDate("dummy.rss"));
        assertTrue(RSSConfiguration.getInstance().getChannelFields("dummy.rss").containsAll(dummyChannelFields));
        assertTrue(dummyChannelFields.containsAll(RSSConfiguration.getInstance().getChannelFields("dummy.rss")));
        assertTrue(RSSConfiguration.getInstance().getItemFields("dummy.rss").containsAll(dummyItemFields));
        assertTrue(dummyItemFields.containsAll(RSSConfiguration.getInstance().getItemFields("dummy.rss")));

        cleanup();
        deleteFile(file);
    }

    @Test
    @DisplayName("Test of ability to handle empty configurations")
    public void autoConfigEmptyConfigTest() throws IOException {
        streamToFile("emptyConfig.cfg");

        AutoRSSConfigurator.setFile(file);
        AutoRSSConfigurator.loadRSSConfiguration();

        assertEquals((Long)RSSConfiguration.defaultTimeToPoll, RSSConfiguration.getInstance().getTimeToPoll());
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());

        cleanup();
        deleteFile(file);
    }

    @Test
    @DisplayName("Test of ability to handle configuration with invalid item pub date")
    public void autoConfigInvalidItemLineConfigInvalidPubDateTest() throws IOException {
        streamToFile("invalidItemLineConfigInvalidPubDate.cfg");

        AutoRSSConfigurator.setFile(file);
        AutoRSSConfigurator.loadRSSConfiguration();

        assertEquals((Long)300L, RSSConfiguration.getInstance().getTimeToPoll());
        assertEquals(2, RSSConfiguration.getInstance().getRSSFeeds().size());

        assertEquals("dummy.txt", RSSConfiguration.getInstance().getRSSFeeds().get("dummy.rss"));
        assertNull(RSSConfiguration.getInstance().getRSSFeedLastPubDate("dummy.rss"));
        assertEquals("newdummy.txt", RSSConfiguration.getInstance().getRSSFeeds().get("newdummy.rss"));
        assertNotNull(RSSConfiguration.getInstance().getRSSFeedLastPubDate("newdummy.rss"));

        cleanup();
        deleteFile(file);
    }

    @Test
    @DisplayName("Test of ability to handle configuration with invalid item line")
    public void autoConfigInvalidItemLineConfigNoFieldTest() throws IOException {
        streamToFile("invalidItemLineConfigNoField.cfg");

        AutoRSSConfigurator.setFile(file);
        AutoRSSConfigurator.loadRSSConfiguration();

        assertEquals((Long)300L, RSSConfiguration.getInstance().getTimeToPoll());
        assertEquals(1, RSSConfiguration.getInstance().getRSSFeeds().size());

        assertEquals("newdummy.txt", RSSConfiguration.getInstance().getRSSFeeds().get("newdummy.rss"));

        cleanup();
        deleteFile(file);
    }

    @Test
    @DisplayName("Test of ability to handle configuration with invalid time to poll")
    public void autoConfigInvalidTimeToPollTest() throws IOException {
        streamToFile("invalidTimeToPollConfig.cfg");

        AutoRSSConfigurator.setFile(file);
        AutoRSSConfigurator.loadRSSConfiguration();

        assertEquals((Long)RSSConfiguration.defaultTimeToPoll, RSSConfiguration.getInstance().getTimeToPoll());
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());

        cleanup();
        deleteFile(file);
    }

}
