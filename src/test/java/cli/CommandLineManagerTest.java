package cli;

import config.RSSConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import poller.Poller;
import validator.RSSFeedValidator;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class CommandLineManagerTest {
    private CommandLineManager manager;

    @Before
    public void setManager() {
        RSSFeedValidator validator = Mockito.mock(RSSFeedValidator.class);
        Mockito.doReturn(true).when(validator).validate("dummy.rss");
        Mockito.doReturn(false).when(validator).validate("newdummy.rss");
        manager = new CommandLineManager(validator);
    }

    @Test
    @DisplayName("Test of creating file if it exists either not")
    public void creatingFileTest() {

        String fileName = "file.txt";
        File f = new File(fileName);
        assertFalse(f.exists());

        manager.createFileIfNotExists(fileName);
        assertTrue(f.exists());

        manager.createFileIfNotExists(fileName);
        assertTrue(f.exists());

        f.delete();
    }

    @Test
    @DisplayName("Test to associate, reassociate and dissociate feeds and files")
    public void associateAndDissociateRssToFileTest() throws ValidationException {
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
        String link = "dummy.rss";
        String file = "dummy.txt";
        manager.associateRssToFile(link, file);
        assertEquals(1, RSSConfiguration.getInstance().getRSSFeeds().size());
        assertEquals(file, RSSConfiguration.getInstance().getRSSFeeds().get(link));

        boolean thrown = false;
        try {
            manager.associateRssToFile("newdummy.rss", file);
        } catch (ValidationException e) {
            thrown = true;
        }
        assertTrue(thrown);

        file = "newdummy.txt";
        manager.reassociateRssToFile(link, file);
        assertEquals(1, RSSConfiguration.getInstance().getRSSFeeds().size());
        assertEquals(file, RSSConfiguration.getInstance().getRSSFeeds().get(link));

        manager.dissociateRss(link);
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
    }

    @Test
    @DisplayName("Test to turn feeds on and off")
    public void turnRSSOnOffTest() throws ValidationException {
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
        String link = "dummy.rss";
        String file = "dummy.txt";
        manager.associateRssToFile(link, file);
        assertTrue(RSSConfiguration.getInstance().isRSSFeedOn(link));

        manager.turnRSSOn(link);
        assertTrue(RSSConfiguration.getInstance().isRSSFeedOn(link));

        manager.turnRSSOff(link);
        assertFalse(RSSConfiguration.getInstance().isRSSFeedOn(link));

        manager.turnRSSOff(link);
        assertFalse(RSSConfiguration.getInstance().isRSSFeedOn(link));

        manager.turnRSSOn(link);
        assertTrue(RSSConfiguration.getInstance().isRSSFeedOn(link));

        manager.dissociateRss(link);
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
    }

    @Test
    @DisplayName("Test to setup different item params")
    public void setRssItemParamsTest() throws ValidationException {
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
        String link = "dummy.rss";
        String file = "dummy.txt";
        manager.associateRssToFile(link, file);
        List<String> params = RSSConfiguration.getInstance().getItemFields(link);

        manager.setRssItemParams(link, new ArrayList<>());
        assertTrue(params.containsAll(RSSConfiguration.getInstance().getItemFields(link)));
        assertTrue(RSSConfiguration.getInstance().getItemFields(link).containsAll(params));

        params = Arrays.asList("title", "description", "pubdate");
        manager.setRssItemParams(link, params);
        assertTrue(params.containsAll(RSSConfiguration.getInstance().getItemFields(link)));
        assertTrue(RSSConfiguration.getInstance().getItemFields(link).containsAll(params));

        params = Arrays.asList("title", "description", "dummyfield");
        List<String> validParams = Arrays.asList("title", "description");
        assertTrue(params.containsAll(RSSConfiguration.getInstance().getChannelFields(link)));
        assertTrue(RSSConfiguration.getInstance().getItemFields(link).containsAll(validParams));
        assertFalse(RSSConfiguration.getInstance().getItemFields(link).containsAll(params));

        manager.dissociateRss(link);
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
    }

    @Test
    @DisplayName("Test to setup different Items max count")
    public void setRssMaxCountTest() throws ValidationException {
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
        String link = "dummy.rss";
        String file = "dummy.txt";
        manager.associateRssToFile(link, file);

        manager.setRSSMaxItems("dummy.rss", 5);
        assertEquals((Integer)5, RSSConfiguration.getInstance().getFeedMaxItems("dummy.rss"));

        manager.dissociateRss(link);
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
    }

    @Test
    @DisplayName("Test to setup different channel params")
    public void setRssChannelParamsTest() throws ValidationException {
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
        String link = "dummy.rss";
        String file = "dummy.txt";
        manager.associateRssToFile(link, file);
        List<String> params = RSSConfiguration.getInstance().getChannelFields(link);

        manager.setRssChannelParams(link, new ArrayList<>());
        assertTrue(params.containsAll(RSSConfiguration.getInstance().getChannelFields(link)));
        assertTrue(RSSConfiguration.getInstance().getChannelFields(link).containsAll(params));

        params = Arrays.asList("title", "description", "pubdate");
        manager.setRssChannelParams(link, params);
        assertTrue(params.containsAll(RSSConfiguration.getInstance().getChannelFields(link)));
        assertTrue(RSSConfiguration.getInstance().getChannelFields(link).containsAll(params));

        params = Arrays.asList("title", "description", "dummyfield");
        List<String> validParams = Arrays.asList("title", "description");
        manager.setRssChannelParams(link, params);
        assertTrue(params.containsAll(RSSConfiguration.getInstance().getChannelFields(link)));
        assertTrue(RSSConfiguration.getInstance().getChannelFields(link).containsAll(validParams));
        assertFalse(RSSConfiguration.getInstance().getChannelFields(link).containsAll(params));

        manager.dissociateRss(link);
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
    }

    @Test
    @DisplayName("Test to setup time to poll")
    public void setTimeToPollTest() {
        Long timeToPollFirst = 10L;
        Long timeToPollSecond = 20L;
        Long timeToPollInvalid = 2L;

        manager.setTimeToPoll(timeToPollFirst);
        assertEquals(timeToPollFirst, RSSConfiguration.getInstance().getTimeToPoll());

        manager.setTimeToPoll(timeToPollSecond);
        assertEquals(timeToPollSecond, RSSConfiguration.getInstance().getTimeToPoll());

        manager.setTimeToPoll(timeToPollInvalid);
        assertEquals((Long)RSSConfiguration.timeCheckThreshold, RSSConfiguration.getInstance().getTimeToPoll());
    }

    @Test
    @DisplayName("Test to print commands")
    public void printCommandsTest() throws ValidationException {
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
        String link = "dummy.rss";
        String file = "dummy.txt";
        manager.associateRssToFile(link, file);

        manager.printHelp();
        manager.printTimeToPoll();
        manager.printRss();
        manager.printAvailableRssItemParams();
        manager.printAvailableRssChannelParams();
        manager.printRssItemParams("dummy.rss");
        manager.printRssChannelParams("dummy.rss");
        manager.printRSSMaxItems("dummy.rss");

        manager.dissociateRss(link);
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
    }
}
