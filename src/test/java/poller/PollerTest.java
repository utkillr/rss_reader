package poller;

import config.RSSConfiguration;
import model.FeedModel;
import model.RSSChannel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import util.PubDateParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;

public class PollerTest {

    private String file = "dummy.txt";

    private FeedModel getRegularFeedModel() {
        FeedModel model = new FeedModel();
        Map<String, String> metaSource = new HashMap<>();
        metaSource.put("description", "dummy description");
        metaSource.put("title", "dummy title");
        Map<String, String> itemSource = new HashMap<>();
        itemSource.put("description", "dummy description");
        itemSource.put("title", "dummy title");
        itemSource.put("pubdate", "Tue, 03 May 2016 11:46:11 EST"); // 1462293971000
        model.metaSource = metaSource;
        model.itemSources.add(itemSource);
        return model;
    }

    private void deleteFile() {
        File f = new File(file);
        f.delete();
    }

    private String readFromFile() throws IOException {
        File f = new File(file);
        return String.join("\n", Files.readAllLines(f.toPath()))
                .replaceAll("\\r\\n|\\n", System.getProperty("line.separator"));
    }

    private String readFromResource(String resource) throws IOException {
        InputStream stream = PollerTest.class.getClassLoader().getResourceAsStream(resource);
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        return new String(bytes).replaceAll("\\r\\n|\\n", System.getProperty("line.separator"));
    }

    @Before
    public void setUp() {
        RSSConfiguration.getInstance().addRSSFeed("dummy.rss", "dummy.txt");
        List<String> channelFields = new ArrayList<>();
        channelFields.add("title");
        channelFields.add("description");
        List<String> itemFields =new ArrayList<>();
        itemFields.add("title");
        itemFields.add("description");
        itemFields.add("pubDate");
        RSSConfiguration.getInstance().reconfig("dummy.rss", itemFields, channelFields);
    }

    @After
    public void tearDown() {
        try {
            RSSConfiguration.getInstance().delRSSFeed("dummy.rss");
        } catch (Exception ignored) {}
        deleteFile();
    }

    @Test
    @DisplayName("Sanity test of that poller can stop")
    public void pollerStopTest() throws InterruptedException {
        Poller poller = new Poller();
        Thread pollingThread = new Thread(poller, "Poller");
        pollingThread.start();
        poller.stop();
        pollingThread.join();
    }

    @Test
    @DisplayName("Test for RSS String format without initial indent")
    public void formRSSStringTestNoIndent() {
        Map<String, String> map = new TreeMap<>();
        map.put("title", "dummy title");
        map.put("description", "dummy description");
        String expected =
                "description:" +
                "\n\tdummy description" +
                "\ntitle:" +
                "\n\tdummy title" +
                "\n\n";
        String result = Poller.getStringFromMap(map, Arrays.asList("description", "title"), 0);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test for RSS String format with initial indent")
    public void formRSSStringTestWithIndent() {
        SortedMap<String, String> map = new TreeMap<>();
        map.put("title", "dummy title");
        map.put("description", "dummy description");
        String expected =
                "\t\tdescription:" +
                "\n\t\t\tdummy description" +
                "\n\t\ttitle:" +
                "\n\t\t\tdummy title" +
                "\n\n";
        String result = Poller.getStringFromMap(map, Arrays.asList("description", "title"), 2);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test RSS String format with not all the fields to read")
    public void formRSSStringTestNotAllFields() {
        Map<String, String> map = new TreeMap<>();
        map.put("title", "dummy title");
        map.put("description", "dummy description");
        map.put("copyright", "dummy copyright");
        String expected =
                "description:" +
                "\n\tdummy description" +
                "\ntitle:" +
                "\n\tdummy title" +
                "\n\n";
        String result = Poller.getStringFromMap(map, Arrays.asList("description", "title"), 0);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test RSS String format with more than all the fields to read")
    public void formRSSStringTestMoreThanAllFields() {
        Map<String, String> map = new TreeMap<>();
        map.put("title", "dummy title");
        map.put("description", "dummy description");
        String expected =
                "description:" +
                "\n\tdummy description" +
                "\ntitle:" +
                "\n\tdummy title" +
                "\n\n";
        String result = Poller.getStringFromMap(map, Arrays.asList("description", "title", "copyright"), 0);
        assertEquals(expected, result);
    }

    // Make Poller methods non-static and test with mockito
    @Test
    @DisplayName("Test to make sure poller can print RSSChannel properly")
    public void printRSSFeedToFileTest() throws IOException {
        String resource = "poller" + File.separator + "regularRss.txt";
        Poller poller = new Poller();
        Path path = new File(file).toPath();
        FeedModel model = getRegularFeedModel();
        RSSChannel channel = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        poller.printRSSFeedToFile(channel, "dummy.rss", path);
        assertEquals(readFromResource(resource), readFromFile());
    }

    @Test
    @DisplayName("Test to make sure poller can print RSSChannel properly in time")
    public void printRSSFeedToFileInTimeTest() throws IOException {
        String resource = "poller" + File.separator + "regularRss.txt";
        Poller poller = new Poller();
        Path path = new File(file).toPath();
        FeedModel model = getRegularFeedModel();
        RSSChannel channel = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        poller.printRSSFeedToFile(channel, "dummy.rss", path);
        // Manually notify since it's not responsibility of poller.printRSSFeedToFile
        RSSConfiguration.getInstance().notifyFeedRead("dummy.rss", PubDateParser.parse("Tue, 03 May 2016 11:46:11 EST"));
        assertEquals(readFromResource(resource), readFromFile());

        Map<String, String> itemSource = new HashMap<>();
        itemSource.put("description", "dummy description");
        itemSource.put("title", "dummy title");
        itemSource.put("pubdate", "Tue, 04 May 2016 11:46:11 EST");
        model.itemSources.add(itemSource);
        resource = "poller" + File.separator + "secondRegularRss.txt";
        channel = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        poller.printRSSFeedToFile(channel, "dummy.rss", path);
        // Manually notify since it's not responsibility of poller.printRSSFeedToFile
        RSSConfiguration.getInstance().notifyFeedRead("dummy.rss", PubDateParser.parse("Tue, 04 May 2016 11:46:11 EST"));
        assertEquals(readFromResource(resource), readFromFile());

        model.itemSources.add(model.itemSources.get(0));
        channel = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        poller.printRSSFeedToFile(channel, "dummy.rss", path);
        // Manually notify since it's not responsibility of poller.printRSSFeedToFile
        // No need since new feed is older than previous
        assertEquals(readFromResource(resource), readFromFile());
    }

    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Test to make sure poller can handle RSSFeed removal")
    public void printRSSFeedToFileNoRSSFeedTest() throws IOException {
        Poller poller = new Poller();
        FeedModel model = getRegularFeedModel();
        RSSChannel channel = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        RSSConfiguration.getInstance().delRSSFeed("dummy.rss");
        Path path = new File(file).toPath();
        poller.printRSSFeedToFile(channel, "dummy.rss", path);
    }

    @Test
    @DisplayName("Test if poller doesn't write to file if no RSS Items found")
    public void printRSSFeedToFileNoRSSItemsTest() throws IOException {
        Poller poller = new Poller();
        FeedModel model = getRegularFeedModel();
        model.itemSources = new ArrayList<>();
        RSSChannel channel = new RSSChannel(RSSConfiguration.getInstance(), "dummy.rss", model);
        Path path = new File(file).toPath();
        poller.printRSSFeedToFile(channel, "dummy.rss", path);
        assertFalse(new File(file).exists());
    }

    @Test
    @DisplayName("Test if poller identifies new pubDate well")
    public void handleRSSFeedTest() throws IOException {
        String resource = "poller" + File.separator + "regularRss.xml";
        InputStream in = PollerTest.class.getClassLoader().getResourceAsStream(resource);

        Poller poller = Mockito.spy(new Poller());
        Mockito.doNothing().when(poller).printRSSFeedToFile(Mockito.any(RSSChannel.class), Mockito.eq("dummy.rss"), Mockito.any(Path.class));
        assertEquals(PubDateParser.parse("Tue, 03 May 2016 11:46:11 EST"), poller.handleRSSFeed(in, "dummy.rss", file));
    }

    @Test
    @DisplayName("Test if Poller can handle invalid URLs")
    public void pollTestBadURL() {
        Poller poller = Mockito.spy(new Poller());
        Mockito.doReturn(null).when(poller).handleRSSFeed(Mockito.any(InputStream.class), Mockito.eq("dummy.rss"), Mockito.eq("dummy.txt"));
        poller.poll(RSSConfiguration.getInstance());
    }
}
