package validator;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import poller.PollerTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RSSFeedValidatorTest {

    private String readFromResource(String resource) throws IOException {
        InputStream stream = PollerTest.class.getClassLoader().getResourceAsStream(resource);
        byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        return new String(bytes).replaceAll("\\r\\n|\\n", System.getProperty("line.separator"));
    }
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    @Test
    @DisplayName("Test if validator can validate valid rss")
    public void validRSSTest() throws IOException {
        String resourceXML = "validator" + File.separator + "regularRss.xml";
        stubForResource(resourceXML);
        RSSFeedValidator validator = new RSSFeedValidator();
        assertTrue(validator.validate("http://localhost:8089/dummy.rss"));
    }

    @Test
    @DisplayName("Test if validator can validate malformed url")
    public void invalidUrlTest() throws IOException {
        RSSFeedValidator validator = new RSSFeedValidator();
        assertFalse(validator.validate("dummy.rss"));
    }

    @Test
    @DisplayName("Test if validator can validate invalid RSS")
    public void invalidRSSTest() throws IOException {
        String resourceXML = "validator" + File.separator + "invalid.xml";
        stubForResource(resourceXML);
        RSSFeedValidator validator = new RSSFeedValidator();
        assertFalse(validator.validate("http://localhost:8089/dummy.rss"));
    }

    @Test
    @DisplayName("Test if validator can validate not all mandatory fields of channel")
    public void noMandatoryFieldsInChannelTest() throws IOException {
        String resourceXML = "validator" + File.separator + "noChannelMandatoryFields.xml";
        stubForResource(resourceXML);
        RSSFeedValidator validator = new RSSFeedValidator();
        assertFalse(validator.validate("http://localhost:8089/dummy.rss"));
    }

    @Test
    @DisplayName("Test if validator can validate not all mandatory fields of item")
    public void noMandatoryFieldsInItemTest() throws IOException {
        String resourceXML = "validator" + File.separator + "noItemMandatoryFields.xml";
        stubForResource(resourceXML);
        RSSFeedValidator validator = new RSSFeedValidator();
        assertFalse(validator.validate("http://localhost:8089/dummy.rss"));
    }

    private void stubForResource(String resource) throws IOException {
        stubFor(
                get(
                        urlEqualTo("/dummy.rss")
                ).willReturn(
                        aResponse()
                                .withBody(readFromResource(resource))
                )
        );
    }
}
