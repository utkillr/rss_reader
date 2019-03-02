package parser;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class AtomEventParserTest {
    @Test(expected = IllegalAccessException.class)
    @DisplayName("Test for regular Atom parsing with wrong entry point")
    public void parseValidAtomTestWrongEntryPoint() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "atomField.xml";
        InputStream inputStream = RSSItemParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        XMLEvent event = eventReader.nextEvent();

        new AtomEventParser().parse(event, eventReader);
        eventReader.close();
    }

    @Test
    @DisplayName("Test for regular Atom parsing")
    public void parseValidAtomTest() throws XMLStreamException, IllegalAccessException {
        String file = "parser" + File.separator + "atomField.xml";
        InputStream inputStream = RSSItemParserTest.class.getClassLoader().getResourceAsStream(file);

        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = inputFactory.createXMLEventReader(inputStream);
        // Skip first <xml> event
        eventReader.nextEvent();

        XMLEvent event = eventReader.nextEvent();

        String excpected = "self : http://atom.href";

        assertEquals(excpected, new AtomEventParser().parse(event, eventReader));

        eventReader.close();
    }

}
