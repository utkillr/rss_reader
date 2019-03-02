package parser;

import javafx.util.Pair;
import util.XMLEventCharactersReader;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AtomEventParser {
    public String parse(XMLEvent event, XMLEventReader reader) throws IllegalAccessException, XMLStreamException {
        if (event.isStartElement()) {

            String prefix = event.asStartElement().getName().getPrefix();
            if (!prefix.equals("atom")) {
                throw new IllegalAccessException("Atom parser don't parse non-atom fields");
            }
            String localPart = event.asStartElement().getName().getLocalPart();

            switch (localPart) {
                case "link": {
                    Iterator<Attribute> attributes = event.asStartElement().getAttributes();
                    String rel = "", href = "";
                    while (attributes.hasNext()) {
                        Attribute attribute = attributes.next();
                        if (attribute.getName().toString().equals("rel")) {
                            rel = attribute.getValue();
                        }
                        if (attribute.getName().toString().equals("href")) {
                            href = attribute.getValue();
                        }
                    }
                    if (rel != null && href != null) return rel + " : " + href;
                    // Just concat due to they are initialized as empty strings
                    else return rel + href;
                }
                default: {
                    return XMLEventCharactersReader.getCharacterData(event, reader);
                }
            }

        } else throw new IllegalAccessException("Atom parser don't parse at EndElements");
    }
}
