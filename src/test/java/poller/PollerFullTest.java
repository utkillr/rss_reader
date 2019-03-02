package poller;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import config.RSSConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class PollerFullTest {

    private String file = "dummy.txt";

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

    private void deleteFile() {
        File f = new File(file);
        f.delete();
    }

    @Before
    public void setUp() {
        RSSConfiguration.getInstance().addRSSFeed("http://localhost:8089/dummy.rss", "dummy.txt");
        List<String> channelFields = new ArrayList<>();
        channelFields.add("title");
        channelFields.add("description");
        List<String> itemFields =new ArrayList<>();
        itemFields.add("title");
        itemFields.add("description");
        itemFields.add("pubDate");
        RSSConfiguration.getInstance().reconfig("http://localhost:8089/dummy.rss", itemFields, channelFields);
    }

    @After
    public void tearDown() {
        try {
            RSSConfiguration.getInstance().delRSSFeed("http://localhost:8089/dummy.rss");
        } catch (Exception ignored) {}
        deleteFile();
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Test
    @DisplayName("Test that Poller can do the poll fine")
    public void pollerFullSanityCheck() throws IOException {
        Poller poller = new Poller();

        String resourceXML = "poller" + File.separator + "regularRss.xml";
        String resourceTXT = "poller" + File.separator + "regularRss.txt";
        stubFor(
                get(
                        urlEqualTo("/dummy.rss")
                ).willReturn(
                        aResponse()
                                .withBody(readFromResource(resourceXML))
                )
        );
        poller.poll(RSSConfiguration.getInstance());
        assertEquals(readFromResource(resourceTXT), readFromFile());

        resourceXML = "poller" + File.separator + "secondRegularRss.xml";
        resourceTXT = "poller" + File.separator + "secondRegularRss.txt";
        stubFor(
                get(
                        urlEqualTo("/dummy.rss")
                ).willReturn(
                        aResponse()
                                .withBody(readFromResource(resourceXML))
                )
        );
        poller.poll(RSSConfiguration.getInstance());
        assertEquals(readFromResource(resourceTXT), readFromFile());

        resourceXML = "poller" + File.separator + "regularRss.xml";
        resourceTXT = "poller" + File.separator + "secondRegularRss.txt";
        stubFor(
                get(
                        urlEqualTo("/dummy.rss")
                ).willReturn(
                        aResponse()
                                .withBody(readFromResource(resourceXML))
                )
        );
        poller.poll(RSSConfiguration.getInstance());
        assertEquals(readFromResource(resourceTXT), readFromFile());

        // Then try faulted XML
        resourceXML = "poller" + File.separator + "invalid.xml";
        resourceTXT = "poller" + File.separator + "secondRegularRss.txt";
        stubFor(
                get(
                        urlEqualTo("/dummy.rss")
                ).willReturn(
                        aResponse()
                                .withBody(readFromResource(resourceXML))
                )
        );
        poller.poll(RSSConfiguration.getInstance());
        assertEquals(readFromResource(resourceTXT), readFromFile());

        // Then try faulted URL
        RSSConfiguration.getInstance().addRSSFeed("dummy.rss", "dummy.txt");
        poller.poll(RSSConfiguration.getInstance());
        assertEquals(readFromResource(resourceTXT), readFromFile());

        RSSConfiguration.getInstance().delRSSFeed("dummy.rss");
    }

    @Test
    @DisplayName("Test that Poller can do the poll fine with Atom")
    public void pollerAtomFullSanityCheck() throws IOException {
        Poller poller = new Poller();

        String resourceXML = "poller" + File.separator + "atomRss.xml";
        String resourceTXT = "poller" + File.separator + "regularRss.txt";
        stubFor(
                get(
                        urlEqualTo("/dummy.rss")
                ).willReturn(
                        aResponse()
                                .withBody(readFromResource(resourceXML))
                )
        );
        poller.poll(RSSConfiguration.getInstance());
        assertEquals(readFromResource(resourceTXT), readFromFile());

        resourceXML = "poller" + File.separator + "secondAtomRss.xml";
        resourceTXT = "poller" + File.separator + "secondRegularRss.txt";
        stubFor(
                get(
                        urlEqualTo("/dummy.rss")
                ).willReturn(
                        aResponse()
                                .withBody(readFromResource(resourceXML))
                )
        );
        poller.poll(RSSConfiguration.getInstance());
        assertEquals(readFromResource(resourceTXT), readFromFile());

        resourceXML = "poller" + File.separator + "atomRss.xml";
        resourceTXT = "poller" + File.separator + "secondRegularRss.txt";
        stubFor(
                get(
                        urlEqualTo("/dummy.rss")
                ).willReturn(
                        aResponse()
                                .withBody(readFromResource(resourceXML))
                )
        );
        poller.poll(RSSConfiguration.getInstance());
        assertEquals(readFromResource(resourceTXT), readFromFile());
    }
}
