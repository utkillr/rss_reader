package cli;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.xml.bind.ValidationException;
import java.util.*;

import static org.junit.Assert.*;


public class CommandLineParserTest {

    private CommandLineManager getMock() throws ValidationException {
        CommandLineManager clm = Mockito.mock(CommandLineManager.class);
        Mockito.doNothing().when(clm).createFileIfNotExists("dummy.txt");
        Mockito.doNothing().when(clm).associateRssToFile("dummy.rss", "dummy.txt");
        Mockito.doNothing().when(clm).dissociateRss("dummy.rss");
        Mockito.doNothing().when(clm).dissociateRss("newdummy.rss");
        Mockito.doNothing().when(clm).reassociateRssToFile("dummy.rss", "dummy.txt");
        Mockito.doNothing().when(clm).turnRSSOn("dummy.rss");
        Mockito.doNothing().when(clm).turnRSSOn("newdummy.rss");
        Mockito.doNothing().when(clm).turnRSSOff("dummy.rss");
        Mockito.doNothing().when(clm).turnRSSOff("newdummy.rss");
        Mockito.doNothing().when(clm).setRssChannelParams(Mockito.eq("dummy.rss"), Mockito.anyListOf(String.class));
        Mockito.doNothing().when(clm).setRssItemParams(Mockito.eq("dummy.rss"), Mockito.anyListOf(String.class));
        Mockito.doNothing().when(clm).setRSSMaxItems(Mockito.eq("dummy.rss"), Mockito.anyInt());

        Mockito.doNothing().when(clm).printRssFile("dummy.rss");
        Mockito.doNothing().when(clm).printRssFile("newdummy.rss");
        Mockito.doNothing().when(clm).printRSSMaxItems("dummy.rss");
        Mockito.doNothing().when(clm).printRssChannelParams("dummy.rss");
        Mockito.doNothing().when(clm).printAvailableRssChannelParams();
        Mockito.doNothing().when(clm).printRssItemParams("dummy.rss");
        Mockito.doNothing().when(clm).printAvailableRssItemParams();
        Mockito.doNothing().when(clm).printRss();
        Mockito.doNothing().when(clm).printHelp();
        Mockito.doNothing().when(clm).saveConfiguration();

        return clm;
    }

    @Test
    @DisplayName("Test to parse add command")
    public void parseRssAddTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        boolean thrown;

        String cmd = "rss add dummy.rss dummy.txt";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).createFileIfNotExists("dummy.txt");
        Mockito.verify(clm, Mockito.times(1)).associateRssToFile("dummy.rss", "dummy.txt");
        Mockito.verify(clm, Mockito.times(0)).setRSSMaxItems("dummy.rss", 100);
        Mockito.verify(clm, Mockito.never()).dissociateRss("dummy.rss");
        Mockito.verify(clm, Mockito.never()).reassociateRssToFile("dummy.rss", "dummy.txt");

        cmd = "rss add dummy.rss dummy.txt 100";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(2)).createFileIfNotExists("dummy.txt");
        Mockito.verify(clm, Mockito.times(2)).associateRssToFile("dummy.rss", "dummy.txt");
        Mockito.verify(clm, Mockito.times(1)).setRSSMaxItems("dummy.rss", 100);
        Mockito.verify(clm, Mockito.never()).dissociateRss("dummy.rss");
        Mockito.verify(clm, Mockito.never()).reassociateRssToFile("dummy.rss", "dummy.txt");

        cmd = "rss add dummy.rss dummy.txt 100 dummy";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        cmd = "rss add dummy.rss dummy.txt dummy";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (NumberFormatException e) {
            thrown = true;
        }
        assertTrue(thrown);

        cmd = "rss add dummy.rss";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        cmd = "rss add";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    @DisplayName("Test to parse del command")
    public void parseRssDelTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        boolean thrown;

        String cmd = "rss del dummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).dissociateRss("dummy.rss");
        Mockito.verify(clm, Mockito.never()).associateRssToFile("dummy.rss", "dummy.txt");
        Mockito.verify(clm, Mockito.never()).reassociateRssToFile("dummy.rss", "dummy.txt");

        cmd = "rss del dummy.rss newdummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(2)).dissociateRss("dummy.rss");
        Mockito.verify(clm, Mockito.times(1)).dissociateRss("newdummy.rss");
        Mockito.verify(clm, Mockito.never()).associateRssToFile("dummy.rss", "dummy.txt");
        Mockito.verify(clm, Mockito.never()).reassociateRssToFile("dummy.rss", "dummy.txt");

        cmd = "rss del";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    @DisplayName("Test to parse on and off commands")
    public void parseOnOffTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        boolean thrown;

        String cmd = "rss on dummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).turnRSSOn("dummy.rss");
        Mockito.verify(clm, Mockito.times(0)).turnRSSOff("dummy.rss");

        cmd = "rss on dummy.rss newdummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(2)).turnRSSOn("dummy.rss");
        Mockito.verify(clm, Mockito.times(1)).turnRSSOn("newdummy.rss");
        Mockito.verify(clm, Mockito.times(0)).turnRSSOff("dummy.rss");
        Mockito.verify(clm, Mockito.times(0)).turnRSSOff("newdummy.rss");

        cmd = "rss off dummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(2)).turnRSSOn("dummy.rss");
        Mockito.verify(clm, Mockito.times(1)).turnRSSOff("dummy.rss");

        cmd = "rss off dummy.rss newdummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(2)).turnRSSOn("dummy.rss");
        Mockito.verify(clm, Mockito.times(1)).turnRSSOn("newdummy.rss");
        Mockito.verify(clm, Mockito.times(2)).turnRSSOff("dummy.rss");
        Mockito.verify(clm, Mockito.times(1)).turnRSSOff("newdummy.rss");

        cmd = "rss on";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        cmd = "rss off";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    @DisplayName("Test to parse file commands")
    public void parseFileTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        boolean thrown;

        String cmd = "rss file dummy.rss dummy.txt";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).reassociateRssToFile("dummy.rss", "dummy.txt");
        Mockito.verify(clm, Mockito.never()).associateRssToFile("dummy.rss", "dummy.txt");
        Mockito.verify(clm, Mockito.never()).dissociateRss("dummy.rss");

        cmd = "rss file dummy.rss dummy.txt dummy";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        cmd = "rss file dummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).reassociateRssToFile("dummy.rss", "dummy.txt");
        Mockito.verify(clm, Mockito.never()).associateRssToFile("dummy.rss", "dummy.txt");
        Mockito.verify(clm, Mockito.never()).dissociateRss("dummy.rss");
        Mockito.verify(clm, Mockito.times(1)).printRssFile("dummy.rss");

        cmd = "rss file";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    @DisplayName("Test to parse channel commands")
    public void parseChannelTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        List<String> params = Arrays.asList("a", "b", "c");

        String cmd = "rss channel";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.never()).setRssChannelParams("dummy.rss", params);
        Mockito.verify(clm, Mockito.times(1)).printAvailableRssChannelParams();
        Mockito.verify(clm, Mockito.never()).printRssChannelParams("dummy.rss");

        cmd = "rss channel dummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.never()).setRssChannelParams("dummy.rss", params);
        Mockito.verify(clm, Mockito.times(1)).printAvailableRssChannelParams();
        Mockito.verify(clm, Mockito.times(1)).printRssChannelParams("dummy.rss");

        cmd = "rss channel dummy.rss a b c";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).setRssChannelParams("dummy.rss", params);
        Mockito.verify(clm, Mockito.times(1)).printAvailableRssChannelParams();
        Mockito.verify(clm, Mockito.times(1)).printRssChannelParams("dummy.rss");
    }

    @Test
    @DisplayName("Test to parse item commands")
    public void parseItemTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        List<String> params = Arrays.asList("a", "b", "c");

        String cmd = "rss item";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.never()).setRssItemParams("dummy.rss", params);
        Mockito.verify(clm, Mockito.times(1)).printAvailableRssItemParams();
        Mockito.verify(clm, Mockito.never()).printRssItemParams("dummy.rss");

        cmd = "rss item dummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.never()).setRssItemParams("dummy.rss", params);
        Mockito.verify(clm, Mockito.times(1)).printAvailableRssItemParams();
        Mockito.verify(clm, Mockito.times(1)).printRssItemParams("dummy.rss");

        cmd = "rss item dummy.rss a b c";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).setRssItemParams("dummy.rss", params);
        Mockito.verify(clm, Mockito.times(1)).printAvailableRssItemParams();
        Mockito.verify(clm, Mockito.times(1)).printRssItemParams("dummy.rss");
    }

    @Test
    @DisplayName("Test to parse max commands")
    public void parseMaxTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        boolean thrown;

        String cmd = "rss max dummy.rss 100";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).setRSSMaxItems("dummy.rss", 100);

        cmd = "rss max dummy.rss 100 dummy";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

        cmd = "rss max dummy.rss dummy";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (NumberFormatException e) {
            thrown = true;
        }
        assertTrue(thrown);

        cmd = "rss max dummy.rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).setRSSMaxItems("dummy.rss", 100);
        Mockito.verify(clm, Mockito.times(1)).printRSSMaxItems("dummy.rss");

        cmd = "rss max";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    @DisplayName("Test to parse rss command")
    public void parseRssTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);

        String cmd = "rss";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).printRss();
    }

    @Test
    @DisplayName("Test to parse time commands")
    public void parseTimeTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        boolean thrown;

        String cmd = "time 1";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.never()).printTimeToPoll();
        Mockito.verify(clm, Mockito.times(1)).setTimeToPoll(1L);

        cmd = "time";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).printTimeToPoll();
        Mockito.verify(clm, Mockito.times(1)).setTimeToPoll(1L);

        cmd = "time 1 1";
        try {
            parser.parse(cmd);
            thrown = false;
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    @DisplayName("Test to parse save command")
    public void parseSaveCmdTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);

        String cmd = "save";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).saveConfiguration();
    }

    @Test
    @DisplayName("Test to parse help command")
    public void parseHelpTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);

        String cmd = "help";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(1)).printHelp();

        cmd = "help dummy";
        parser.parse(cmd);
        Mockito.verify(clm, Mockito.times(2)).printHelp();
    }

    @Test
    @DisplayName("Test to parse exit command")
    public void parseExitTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);

        String cmd = "exit";
        assertEquals(1, parser.parse(cmd));

        cmd = "exit dummy";
        assertEquals(1, parser.parse(cmd));
    }

    @Test(expected = IllegalArgumentException.class)
    @DisplayName("Test to parse dummy command")
    public void parseDummyTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        String cmd = "dummy";
        parser.parse(cmd);
    }

    @Test
    @DisplayName("Test to parse empty command")
    public void parseEmptyCmdTest() throws ValidationException {
        CommandLineManager clm = getMock();
        CommandLineParser parser = new CommandLineParser(clm);
        assertEquals(0, parser.parse(""));
        assertEquals(0, parser.parse(" "));
        assertEquals(0, parser.parse("  "));
        assertEquals(0, parser.parse("\t"));
    }
}
