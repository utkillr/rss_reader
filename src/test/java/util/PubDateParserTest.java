package util;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.Assert.*;

public class PubDateParserTest {

    @Test
    @DisplayName("Test abilities of pubDate parser")
    public void pubDateParserTest() {
        assertNotNull(PubDateParser.parse("Tue, 03 May 2016 11:46:11 +0200"));
        assertNotNull(PubDateParser.parse("Tue, 03 May 2016 11:46:11 EST"));

        assertNotNull(PubDateParser.parse("Tue, 03 May 2016 11:46 +0200"));
        assertNotNull(PubDateParser.parse("Tue, 03 May 2016 11:46 EST"));

        assertNotNull(PubDateParser.parse("Tue May 03 11:46:11 2016 +0200"));
        assertNotNull(PubDateParser.parse("Tue May 03 11:46:11 2016 EST"));

        assertNotNull(PubDateParser.parse("Tue May 03 11:46 2016 +0200"));
        assertNotNull(PubDateParser.parse("Tue May 03 11:46 2016 EST"));

        assertNotNull(PubDateParser.parse("Tue May 03 11:46:11 +0200 2016"));
        assertNotNull(PubDateParser.parse("Tue May 03 11:46:11 EST 2016"));

        assertNotNull(PubDateParser.parse("Tue May 03 11:46 +0200 2016"));
        assertNotNull(PubDateParser.parse("Tue May 03 11:46 EST 2016"));

        assertNull(PubDateParser.parse("dummy"));
    }
}
