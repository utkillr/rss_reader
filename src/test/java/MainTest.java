import config.AutoRSSConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class MainTest {

    private String file = "test.cfg";

    private InputStream old;

    @Before
    public void setUp() {
        old = System.in;
        AutoRSSConfigurator.setFile(file);
    }

    @After
    public void tearDown() {
        System.setIn(old);
        File f = new File(file);
        f.delete();
    }

    @Test
    @DisplayName("Sanity Main test")
    public void mainSanityTest() throws InterruptedException {
        String data = "exit";
        InputStream testInput = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        System.setIn(testInput);
        Thread mainThread = new Thread(() -> Main.main(new String[]{}));
        mainThread.start();
        TimeUnit.SECONDS.sleep(1);
        mainThread.join();
    }
}
