package parser;

import model.FeedModel;
import util.Log;
import util.XMLEventCharactersReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * This class implements parsing for item properties
 */
class RSSItemParser {
    private static Log log = new Log(RSSItemParser.class.getName(), System.out);

    /**
     * Iterate over XML and write item properties.
     * Finish on closing the item tag
     *
     * Note: This parser is waiting for eventReader to be pointed after item tag is opened
     *
     * @param eventReader XMLEventReader pointing right after item tag opening
     * @return map: ItemProperty -> value
     */
    Map<String, String> parse(XMLEvent event, XMLEventReader eventReader) throws IllegalAccessException, XMLStreamException {
        Map<String, String> model = new HashMap<>();
        if (!(event.isStartElement() && (
                event.asStartElement().getName().getLocalPart().equals(FeedModel.FEED_ITEM)
                        || event.asStartElement().getName().getLocalPart().equals(FeedModel.ATOM_ITEM)
        ))) {
            throw new IllegalAccessException("Not an <item> tag");
        }
        try {
            while (eventReader.hasNext()) {
                event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String prefix = event.asStartElement().getName().getPrefix();
                    String localPart = event.asStartElement().getName().getLocalPart();
                    if (prefix.equals("atom")) {
                        model.put(
                                (prefix + ":" + localPart).toLowerCase(),
                                new AtomEventParser().parse(event, eventReader)
                        );
                        continue;
                    }
                    try {
                        model.put(
                                localPart.toLowerCase(),
                                XMLEventCharactersReader.getCharacterData(event, eventReader)
                        );
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage());
                    }
                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getLocalPart().equals(FeedModel.FEED_ITEM)) {
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.error("Error occurred during parsing XML items and writing item properties: " + e.getMessage());
            throw e;
        }
        return model;
    }
}
