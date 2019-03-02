package config;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.Assert.*;

public class LocalFeedInfoTest {

    @Test
    @DisplayName("Test LocalFeedInfo initialization")
    public void localFeedInfoInitTest() {
        LocalFeedInfo info = new LocalFeedInfo();
        assertEquals("ON", info.status.toString());
        assertNull(info.lastPubDate);

        info = new LocalFeedInfo("ON", 1000000L);
        assertEquals("ON", info.status.toString());
        assertEquals(1000000L, info.lastPubDate.getTime());

        info = new LocalFeedInfo("OFF", 2000000L);
        assertEquals("OFF", info.status.toString());
        assertEquals(2000000L, info.lastPubDate.getTime());
    }
}
