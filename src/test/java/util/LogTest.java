package util;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class LogTest {

    @Before
    public void setUp() {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bo));
    }

    private String getLastStringAfterSubstring(String string, String substring) {
        return string.substring(string.indexOf(substring) + substring.length());
    }

    @Test
    public void logTest() throws IOException {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(bo);
        Log log = new Log("TEST", stream);

        log.info("info");
        bo.flush();
        String allWrittenLines = new String(bo.toByteArray());
        String expected = "[TEST]: [INFO] info\r\n";
        assertEquals(expected, allWrittenLines);

        log.response("response");
        bo.flush();
        allWrittenLines = getLastStringAfterSubstring(new String(bo.toByteArray()), expected);
        expected = "[TEST]: [RESPONSE] response\r\n";
        assertEquals(expected, allWrittenLines);

        log.warn("warn");
        bo.flush();
        allWrittenLines = getLastStringAfterSubstring(new String(bo.toByteArray()), expected);
        expected = "[TEST]: [WARN] warn\r\n";
        assertEquals(expected, allWrittenLines);

        log.error("error");
        bo.flush();
        allWrittenLines = getLastStringAfterSubstring(new String(bo.toByteArray()), expected);
        expected = "[TEST]: [ERROR] error\r\n";
        assertEquals(expected, allWrittenLines);

        log.emptyLine();
        bo.flush();
        allWrittenLines = getLastStringAfterSubstring(new String(bo.toByteArray()), expected);
        expected = "\r\n";
        assertEquals(expected, allWrittenLines);
    }
}
