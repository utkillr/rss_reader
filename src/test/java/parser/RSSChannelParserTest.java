package parser;

import model.FeedModel;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RSSChannelParserTest {
    @Test(expected = IllegalAccessException.class)
    @DisplayName("Test for regular RSSChannel parsing with wrong entry point")
    public void parseValidRSSChannelTestWrongEntryPoint() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "regularChannel.xml";
        InputStream inputStream = RSSChannelParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        XMLEvent event = eventReader.nextEvent();

        new RSSItemParser().parse(event, eventReader);
        eventReader.close();
    }

    @Test
    @DisplayName("Test for regular RSSChannel parsing")
    public void parseValidRSSChannelTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "regularChannel.xml";
        InputStream inputStream = RSSChannelParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        FeedModel expectedModel  = new FeedModel();
        expectedModel.metaSource.put("title", "CHANNEL NAME");
        expectedModel.metaSource.put("description", "CHANNEL DESCRIPTION");
        Map<String, String> source = new HashMap<>();
        source.put("title", "NAME");
        source.put("description", "DESCRIPTION");
        source.put("pubdate", "DATE");
        expectedModel.itemSources.add(source);
        FeedModel model = new RSSChannelParser().parse(event, eventReader);

        assertEquals(expectedModel.metaSource.size(), model.metaSource.size());
        for (String key : expectedModel.metaSource.keySet()) {
            assertEquals(expectedModel.metaSource.get(key), model.metaSource.get(key));
        }
        assertEquals(expectedModel.itemSources.size(), model.itemSources.size());

        eventReader.close();
    }

    @Test
    @DisplayName("Test for regular RSSChannel parsing without items")
    public void parseValidRSSChannelTestWithoutItems() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "channelWithoutItems.xml";
        InputStream inputStream = RSSChannelParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        FeedModel expectedModel  = new FeedModel();
        expectedModel.metaSource.put("title", "CHANNEL NAME");
        expectedModel.metaSource.put("description", "CHANNEL DESCRIPTION");

        FeedModel model = new RSSChannelParser().parse(event, eventReader);

        assertEquals(expectedModel.metaSource.size(), model.metaSource.size());
        for (String key : expectedModel.metaSource.keySet()) {
            assertEquals(expectedModel.metaSource.get(key), model.metaSource.get(key));
        }
        assertEquals(expectedModel.itemSources.size(), model.itemSources.size());

        eventReader.close();
    }

    @Test
    @DisplayName("Test for regular RSSChannel parsing with multiple items")
    public void parseValidRSSChannelTestMultipleItems() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "channelWithMultipleItems.xml";
        InputStream inputStream = RSSChannelParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        FeedModel expectedModel  = new FeedModel();
        expectedModel.metaSource.put("title", "CHANNEL NAME");
        expectedModel.metaSource.put("description", "CHANNEL DESCRIPTION");
        Map<String, String> source = new HashMap<>();
        source.put("title", "NAME");
        source.put("description", "DESCRIPTION");
        source.put("pubdate", "DATE");
        expectedModel.itemSources.add(source);
        expectedModel.itemSources.add(source);
        FeedModel model = new RSSChannelParser().parse(event, eventReader);

        assertEquals(expectedModel.metaSource.size(), model.metaSource.size());
        for (String key : expectedModel.metaSource.keySet()) {
            assertEquals(expectedModel.metaSource.get(key), model.metaSource.get(key));
        }
        assertEquals(expectedModel.itemSources.size(), model.itemSources.size());

        eventReader.close();
    }

    @Test
    @DisplayName("Test for ability to parse RSSItem with sub tag")
    public void parseRSSChannelWithSubTagTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "channelWithSubTag.xml";
        InputStream inputStream = RSSChannelParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        new RSSChannelParser().parse(event, eventReader);
        eventReader.close();
    }

    @Test
    @DisplayName("Test for ability to parse RSSChannel with sub channel")
    public void parseRSSChannelWithSubChannelTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "channelWithSubChannel.xml";
        InputStream inputStream = RSSChannelParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        new RSSChannelParser().parse(event, eventReader);
        eventReader.close();
    }

    @Test
    @DisplayName("Test for Empty RSSChannel parsing")
    public void parseEmptyRSSChannelTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "emptyChannel.xml";
        InputStream inputStream = RSSChannelParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        FeedModel model = new RSSChannelParser().parse(event, eventReader);
        assertTrue(model.metaSource.isEmpty());
        assertTrue(model.itemSources.isEmpty());
        eventReader.close();
    }

    @Test
    @DisplayName("Test for RSSChannel with Atom parsing")
    public void parseAtomRSSChannelTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "channelWithAtomFields.xml";
        InputStream inputStream = RSSChannelParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        FeedModel expectedModel  = new FeedModel();
        expectedModel.metaSource.put("atom:title", "NAME");
        expectedModel.metaSource.put("atom:link", "self : http://atom.href");
        Map<String, String> source = new HashMap<>();
        source.put("title", "NAME");
        source.put("description", "DESCRIPTION");
        source.put("pubdate", "DATE");
        expectedModel.itemSources.add(source);
        FeedModel model = new RSSChannelParser().parse(event, eventReader);

        assertEquals(expectedModel.metaSource.size(), model.metaSource.size());
        for (String key : expectedModel.metaSource.keySet()) {
            assertEquals(expectedModel.metaSource.get(key), model.metaSource.get(key));
        }
        assertEquals(expectedModel.itemSources.size(), model.itemSources.size());

        eventReader.close();
    }
}
