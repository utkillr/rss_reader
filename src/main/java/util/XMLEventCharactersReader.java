package util;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.StringWriter;

public class XMLEventCharactersReader {
    /**
     * Gets character data inside of xml tag via event reader
     * Waits for XMLEvent pointing on opening tag
     *
     * @param event pointing on tag which shall be turned into String
     * @param eventReader active XMLEventReader pointing on on event to be read
     * @return String representation of tag insides
     * @throws XMLStreamException in case of issues with event reading
     * @throws IllegalArgumentException in case this is not StartElement
     */
    public static String getCharacterData(XMLEvent event, XMLEventReader eventReader)
            throws XMLStreamException, IllegalAccessException {
        StringWriter writer = new StringWriter();
        XMLEventWriter eventWriter = XMLOutputFactory.newInstance().createXMLEventWriter(writer);
        if (event.isStartElement()) {
            event = eventReader.nextEvent();
            // in case it's <tag />
            if (event.isEndElement()) return "";
            int depth = 1;
            while (eventReader.hasNext() && depth > 0) {
                if (event.isCharacters() && event.asCharacters().isWhiteSpace()) {
                    // do nothing
                } else {
                    eventWriter.add(event);
                }
                event = eventReader.nextEvent();
                if (event.isStartElement()) depth++;
                if (event.isEndElement()) depth--;
            }
        } else {
            throw new IllegalAccessException("Can't identify which event to parse");
        }
        // Move to next tag
        if (eventReader.hasNext()) eventReader.nextEvent();
        eventWriter.flush();
        writer.flush();
        return writer.getBuffer().toString();
    }
}
