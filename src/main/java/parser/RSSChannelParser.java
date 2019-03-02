package parser;

import javafx.util.Pair;
import model.FeedModel;
import util.Log;
import util.XMLEventCharactersReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.Map;

/**
 * This class implements parsing for channel properties and items
 */
class RSSChannelParser {
    private static Log log = new Log(RSSChannelParser.class.getName(), System.out);


    /**
     * Iterate over XML and write channel properties until it's "item" tag.
     * Then call parser for RSSItem (in loop)
     * Finish on closing the channel tag
     *
     * Note: This parser is waiting for eventReader to be pointed after channel tag is opened
     *
     * @param eventReader XMLEventReader pointing right after channel tag opening
     * @return parsed FeedModel
     */
    FeedModel parse(XMLEvent event, XMLEventReader eventReader) throws IllegalAccessException {
        FeedModel model = new FeedModel();
        if (!(event.isStartElement() && (
                event.asStartElement().getName().getLocalPart().equals(FeedModel.FEED_CHANNEL)
                        || event.asStartElement().getName().getLocalPart().equals(FeedModel.ATOM_CHANNEL)
        ))) {
            throw new IllegalAccessException("Not an <channel> tag");
        }
        try {
            while (eventReader.hasNext()) {
                event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String prefix = event.asStartElement().getName().getPrefix();
                    String localPart = event.asStartElement().getName().getLocalPart();
                    // in case of it is <item>
                    if (localPart.equals(FeedModel.FEED_ITEM) || localPart.equals(FeedModel.ATOM_ITEM)) {
                        try {
                            model.itemSources.add(new RSSItemParser().parse(event, eventReader));
                        } catch (IllegalAccessException e) {
                            log.error(e.getMessage());
                        }
                    } else if (prefix.equals("atom")) {
                        model.metaSource.put(
                                (prefix + ":" + localPart).toLowerCase(),
                                new AtomEventParser().parse(event, eventReader)
                        );
                    } else {
                        try {
                            model.metaSource.put(
                                    localPart.toLowerCase(),
                                    XMLEventCharactersReader.getCharacterData(event, eventReader)
                            );
                        } catch (IllegalAccessException e) {
                            log.error(e.getMessage());
                        }
                    }
                } else if (event.isEndElement()){
                    if (event.asEndElement().getName().getLocalPart().equals(FeedModel.FEED_CHANNEL)) {
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.error("Error occurred during parsing XML items and writing channel properties: " + e.getMessage());
        }
        return model;
    }
}
