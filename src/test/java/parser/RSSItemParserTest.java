package parser;

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

public class RSSItemParserTest {

    @Test(expected = IllegalAccessException.class)
    @DisplayName("Test for regular RSSItem parsing with wrong entry point")
    public void parseValidRSSItemTestWrongEntryPoint() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "regularItem.xml";
        InputStream inputStream = RSSItemParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        XMLEvent event = eventReader.nextEvent();

        new RSSItemParser().parse(event, eventReader);
        eventReader.close();
    }

    @Test
    @DisplayName("Test for regular RSSItem parsing")
    public void parseValidRSSItemTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "regularItem.xml";
        InputStream inputStream = RSSItemParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        Map<String, String> expectedModel = new HashMap<>();
        expectedModel.put("title", "NAME");
        expectedModel.put("description", "DESCRIPTION");
        expectedModel.put("pubdate", "DATE");
        Map<String, String> model = new RSSItemParser().parse(event, eventReader);

        assertEquals(expectedModel.size(), model.size());
        for (String key : expectedModel.keySet()) {
            assertEquals(expectedModel.get(key), model.get(key));
        }

        eventReader.close();
    }

    @Test
    @DisplayName("Test for ability to parse RSSItem with sub tag")
    public void parseRSSItemWithSubTagTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "itemWithSubTag.xml";
        InputStream inputStream = RSSItemParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        new RSSItemParser().parse(event, eventReader);
        eventReader.close();
    }

    @Test
    @DisplayName("Test for ability to parse RSSItem with sub item")
    public void parseRSSItemWithSubItemTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "itemWithSubItem.xml";
        InputStream inputStream = RSSItemParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        new RSSItemParser().parse(event, eventReader);
        eventReader.close();
    }

    @Test
    @DisplayName("Test for Empty RSSItem parsing")
    public void parseEmptyRSSItemTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "emptyItem.xml";
        InputStream inputStream = RSSItemParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        Map<String, String> model = new RSSItemParser().parse(event, eventReader);
        assertTrue(model.isEmpty());
        eventReader.close();
    }

    @Test
    @DisplayName("Test for Atom RSSItem is parsed")
    public void parseAtomRSSItemTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "itemWithAtomFields.xml";
        InputStream inputStream = RSSItemParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        Map<String, String> expectedModel = new HashMap<>();
        expectedModel.put("atom:title", "NAME");
        expectedModel.put("atom:link", "self : http://atom.href");
        Map<String, String> model = new RSSItemParser().parse(event, eventReader);

        assertEquals(expectedModel.size(), model.size());
        for (String key : expectedModel.keySet()) {
            assertEquals(expectedModel.get(key), model.get(key));
        }

        eventReader.close();
    }

}
