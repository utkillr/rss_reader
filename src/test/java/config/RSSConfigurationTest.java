package config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class RSSConfigurationTest {

    @Before
    public void setUp() {
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
        RSSConfiguration.getInstance().addRSSFeed("dummy.rss", "dummy.txt");
    }

    @After
    public void tearDown() {
        RSSConfiguration.getInstance().delRSSFeed("dummy.rss");
        assertTrue(RSSConfiguration.getInstance().getRSSFeeds().isEmpty());
    }

    @Test
    @DisplayName("Test ability of adding new RSS Feed")
    public void addRemoveRSSFeedTest() {
        assertEquals("dummy.txt", RSSConfiguration.getInstance().getRSSFeeds().get("dummy.rss"));
        assertTrue(RSSConfiguration.getInstance().isRSSFeedOn("dummy.rss"));
    }

    @Test
    @DisplayName("Test ability of RSS Feed reconfiguration")
    public void reconfigTest() {
        List<String> validChannelParams = Arrays.asList("description", "copyright");
        List<String> validItemParams = Arrays.asList("description", "title");
        RSSConfiguration.getInstance().reconfig("dummy.rss", validItemParams, validChannelParams);
        assertTrue(RSSConfiguration.getInstance().getChannelFields("dummy.rss").containsAll(validChannelParams));
        assertTrue(validChannelParams.containsAll(RSSConfiguration.getInstance().getChannelFields("dummy.rss")));
        assertTrue(RSSConfiguration.getInstance().getItemFields("dummy.rss").containsAll(validItemParams));
        assertTrue(validItemParams.containsAll(RSSConfiguration.getInstance().getItemFields("dummy.rss")));

        List<String> notOnlyValidChannelParams = Arrays.asList("description", "copyright", "dummy");
        List<String> notOnlyValidItemParams = Arrays.asList("description", "title", "dummy");
        RSSConfiguration.getInstance().reconfig("dummy.rss", notOnlyValidItemParams, notOnlyValidChannelParams);
        assertFalse(RSSConfiguration.getInstance().getChannelFields("dummy.rss").containsAll(notOnlyValidChannelParams));
        assertTrue(RSSConfiguration.getInstance().getChannelFields("dummy.rss").containsAll(validChannelParams));
        assertTrue(notOnlyValidChannelParams.containsAll(RSSConfiguration.getInstance().getChannelFields("dummy.rss")));
        assertFalse(RSSConfiguration.getInstance().getItemFields("dummy.rss").containsAll(notOnlyValidItemParams));
        assertTrue(RSSConfiguration.getInstance().getItemFields("dummy.rss").containsAll(validItemParams));
        assertTrue(notOnlyValidItemParams.containsAll(RSSConfiguration.getInstance().getItemFields("dummy.rss")));

        List<String> invalidChannelParams = Arrays.asList("dummy");
        List<String> invalidItemParams = Arrays.asList("dummy");
        RSSConfiguration.getInstance().reconfig("dummy.rss", invalidItemParams, invalidChannelParams);
        assertFalse(RSSConfiguration.getInstance().getChannelFields("dummy.rss").containsAll(invalidChannelParams));
        assertFalse(invalidChannelParams.containsAll(RSSConfiguration.getInstance().getChannelFields("dummy.rss")));
        assertTrue(RSSConfiguration.getInstance().getChannelFields("dummy.rss").containsAll(validChannelParams));
        assertTrue(validChannelParams.containsAll(RSSConfiguration.getInstance().getChannelFields("dummy.rss")));
        assertFalse(RSSConfiguration.getInstance().getItemFields("dummy.rss").containsAll(invalidItemParams));
        assertFalse(invalidItemParams.containsAll(RSSConfiguration.getInstance().getItemFields("dummy.rss")));
        assertTrue(RSSConfiguration.getInstance().getItemFields("dummy.rss").containsAll(validItemParams));
        assertTrue(validItemParams.containsAll(RSSConfiguration.getInstance().getItemFields("dummy.rss")));

        RSSConfiguration.getInstance().reconfig("dummy.rss", null, null);
        assertTrue(RSSConfiguration.getInstance().getChannelFields("dummy.rss").containsAll(validChannelParams));
        assertTrue(validChannelParams.containsAll(RSSConfiguration.getInstance().getChannelFields("dummy.rss")));
        assertTrue(RSSConfiguration.getInstance().getItemFields("dummy.rss").containsAll(validItemParams));
        assertTrue(validItemParams.containsAll(RSSConfiguration.getInstance().getItemFields("dummy.rss")));
    }

    @Test
    @DisplayName("Test ability of turning RSS Feed On and Off")
    public void turnRSSFeedOnOffTest() {
        RSSConfiguration.getInstance().turnOnRSSFeed("dummy.rss");
        assertTrue(RSSConfiguration.getInstance().isRSSFeedOn("dummy.rss"));
        RSSConfiguration.getInstance().turnOffRSSFeed("dummy.rss");
        assertFalse(RSSConfiguration.getInstance().isRSSFeedOn("dummy.rss"));
        RSSConfiguration.getInstance().turnOffRSSFeed("dummy.rss");
        assertFalse(RSSConfiguration.getInstance().isRSSFeedOn("dummy.rss"));
        RSSConfiguration.getInstance().turnOnRSSFeed("dummy.rss");
        assertTrue(RSSConfiguration.getInstance().isRSSFeedOn("dummy.rss"));
    }

    @Test
    @DisplayName("Test ability of changing latest PubDate")
    public void changePubDateTest() {
        RSSConfiguration.getInstance().notifyFeedRead("dummy.rss", null);
        assertNull(RSSConfiguration.getInstance().getRSSFeedLastPubDate("dummy.rss"));
        Date dummyPubDate = new Date(1000000L);
        RSSConfiguration.getInstance().notifyFeedRead("dummy.rss", dummyPubDate);
        assertEquals(dummyPubDate, RSSConfiguration.getInstance().getRSSFeedLastPubDate("dummy.rss"));
    }

    @Test
    @DisplayName("Test ability of changing file")
    public void changeFileTest() {
        RSSConfiguration.getInstance().setRSSFeedFile("dummy.rss", "newfile.txt");
        assertEquals("newfile.txt", RSSConfiguration.getInstance().getRSSFeeds().get("dummy.rss"));
    }

    @Test
    @DisplayName("Test ability of changing max items count")
    public void changeMaxItemsTest() {
        assertEquals((Integer)RSSConfiguration.defaultMaxItems, RSSConfiguration.getInstance().getFeedMaxItems("dummy.rss"));
        RSSConfiguration.getInstance().setFeedMaxItems("dummy.rss", 5);
        assertEquals((Integer)5, RSSConfiguration.getInstance().getFeedMaxItems("dummy.rss"));
    }

    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Test ability of changing max items count to throw")
    public void changeMaxItemsTestThrow() {
        assertEquals((Integer)RSSConfiguration.defaultMaxItems, RSSConfiguration.getInstance().getFeedMaxItems("dummy.rss"));
        RSSConfiguration.getInstance().setFeedMaxItems("dummy.rss", -1);
    }

    @Test
    @DisplayName("Test ability of changing polling time")
    public void changePollTimeTest() {
        RSSConfiguration.getInstance().setTimeToPoll(100L);
        assertEquals((Long)100L, RSSConfiguration.getInstance().getTimeToPoll());
        RSSConfiguration.getInstance().setTimeToPoll(1L);
        assertEquals((Long)RSSConfiguration.timeCheckThreshold, RSSConfiguration.getInstance().getTimeToPoll());
        RSSConfiguration.getInstance().setTimeToPoll(100L);
        assertEquals((Long)100L, RSSConfiguration.getInstance().getTimeToPoll());
    }

    @Test
    @DisplayName("Test disability of config lists to be modified")
    public void unmodifiableListsTest() {
        assertTrue(getUnsupportedOperationException(RSSConfiguration.getRawMandatoryChannelFields()::add, "dummy"));
        assertTrue(getUnsupportedOperationException(RSSConfiguration.getRawMandatoryItemFields()::add, "dummy"));
        assertTrue(getUnsupportedOperationException(RSSConfiguration.getAvailableChannelFields()::add, "dummy"));
        assertTrue(getUnsupportedOperationException(RSSConfiguration.getAvailableItemFields()::add, "dummy"));
        assertTrue(getUnsupportedOperationException(RSSConfiguration.getInstance().getChannelFields("dummy.rss")::add, "dummy"));
        assertTrue(getUnsupportedOperationException(RSSConfiguration.getInstance().getItemFields("dummy.rss")::add, "dummy"));
    }

    private boolean getUnsupportedOperationException(Consumer<String> method, String arg) {
        try {
            method.accept(arg);
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }

    @Test
    @DisplayName("Test check for Feed existence")
    public void feedExistenceTest() {
        boolean thrown = false;
        try {
            RSSConfiguration.getInstance().reconfig("newdummy.rss", null, null);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        thrown = false;
        try {
            RSSConfiguration.getInstance().addRSSFeed("dummy.rss", "dummy.txt");
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        thrown = false;
        try {
            RSSConfiguration.getInstance().notifyFeedRead("newdummy.rss", null);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        thrown = false;
        try {
            RSSConfiguration.getInstance().setRSSFeedFile("newdummy.rss", "newdummy.txt");
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        thrown = false;
        try {
            RSSConfiguration.getInstance().setFeedMaxItems("newdummy.rss", null);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        assertTrue(getIllegalArgumentException(RSSConfiguration.getInstance()::delRSSFeed, "newdummy.rss"));
        assertTrue(getIllegalArgumentException(RSSConfiguration.getInstance()::isRSSFeedOn, "newdummy.rss"));
        assertTrue(getIllegalArgumentException(RSSConfiguration.getInstance()::turnOnRSSFeed, "newdummy.rss"));
        assertTrue(getIllegalArgumentException(RSSConfiguration.getInstance()::turnOffRSSFeed, "newdummy.rss"));
        assertTrue(getIllegalArgumentException(RSSConfiguration.getInstance()::getRSSFeedLastPubDate, "newdummy.rss"));
        assertTrue(getIllegalArgumentException(RSSConfiguration.getInstance()::getChannelFields, "newdummy.rss"));
        assertTrue(getIllegalArgumentException(RSSConfiguration.getInstance()::getItemFields, "newdummy.rss"));
        assertTrue(getIllegalArgumentException(RSSConfiguration.getInstance()::getFeedMaxItems, "newdummy.rss"));
    }

    private boolean getIllegalArgumentException(Consumer<String> method, String arg) {
        try {
            method.accept(arg);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    @Test
    @DisplayName("Test Atom to RSS conversion")
    public void atomToRssTest() {
        assertEquals("pubdate", RSSConfiguration.atomToRSS("atom:published"));
        assertEquals("pubdate", RSSConfiguration.atomToRSS("atom:updated"));
        assertEquals("description", RSSConfiguration.atomToRSS("atom:content"));
        assertEquals("copyright", RSSConfiguration.atomToRSS("atom:rights"));
        assertEquals("", RSSConfiguration.atomToRSS("atom:dummy"));
    }
}
