package parser;

import model.FeedModel;
import util.Log;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

/**
 * This class implements global parsing to throw away all the data besides "channel" tag
 */
public class FeedModelParser {
    private static Log log = new Log(FeedModelParser.class.getName(), System.out);
    /**
     * Iterate over XML until it's "channel" tag. Then call parser for RSSChannel
     *
     * @param in InputStream with XML
     * @return model which is returned from RSSChannelParser
     */
    public FeedModel parse(InputStream in) {
        FeedModel model = new FeedModel();
        try {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    String localPart = event.asStartElement().getName().getLocalPart();
                    if (localPart.equals(FeedModel.FEED_CHANNEL) || localPart.equals((FeedModel.ATOM_CHANNEL))) {
                        model = new RSSChannelParser().parse(event, eventReader);
                        // Break to ignore anything beyond channel
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            log.error("Error occurred during parsing RSS Feed: " + e.getMessage());
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        }
        return model;
    }
}
