package util;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;

public class XMLEventCharactersReaderTest {

    @Test
    @DisplayName("Test reading of single event")
    public void openingEventReadTest() throws XMLStreamException, IllegalAccessException {
        String file = "util" + File.separator + "singleEvent.xml";
        InputStream inputStream = XMLEventCharactersReaderTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();
        String expected = "NASA";
        assertEquals(expected, XMLEventCharactersReader.getCharacterData(event, eventReader));

        eventReader.close();
    }

    @Test
    @DisplayName("Test reading of complex event")
    public void openingComplexEventReadTest() throws XMLStreamException, IllegalAccessException {
        String file = "util" + File.separator + "complexEvent.xml";
        InputStream inputStream = XMLEventCharactersReaderTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();
        String expected = "<title>NASA</title>" +
                "<description>Spaced text</description>" +
                "<link>Multiline\n" +
                "    text</link>";
        assertEquals(expected, XMLEventCharactersReader.getCharacterData(event, eventReader));

        eventReader.close();
    }

    @Test(expected = IllegalAccessException.class)
    @DisplayName("Test reading of text event")
    public void textEventReadTest() throws XMLStreamException, IllegalAccessException {
        String file = "util" + File.separator + "singleEvent.xml";
        InputStream inputStream = XMLEventCharactersReaderTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        eventReader.nextEvent();
        XMLEvent event = eventReader.nextEvent();
        XMLEventCharactersReader.getCharacterData(event, eventReader);

        eventReader.close();
    }

    @Test(expected = IllegalAccessException.class)
    @DisplayName("Test reading of single event")
    public void closingEventReadTest() throws XMLStreamException, IllegalAccessException {
        String file = "util" + File.separator + "singleEvent.xml";
        InputStream inputStream = XMLEventCharactersReaderTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        eventReader.nextEvent();
        eventReader.nextEvent();
        XMLEvent event = eventReader.nextEvent();
        XMLEventCharactersReader.getCharacterData(event, eventReader);

        eventReader.close();
    }
}