package model;

import config.RSSConfiguration;
import util.PubDateParser;

import java.io.InvalidObjectException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents RSS Channel (Atom syntax is ignored for now).
 */
public class RSSItem {

    // Actually HashMap to support sorting
    private Map<String, String> body;
    private Date latestPubDate;

    /**
     * Get properties of RSS Item
     *
     * @return map: Configured property -> value
     */
    public Map<String, String> getBody() {
        return body;
    }

    /**
     * Get parsed pubDate, which is assigned in constructor.
     * Field is called latestPubDate for confuse decreasing - the same name is used in RSSChannel
     *
     * @return pubDate
     */
    Date getLatestPubDate() {
        return latestPubDate;
    }

    /**
     * Setup configured item fields
     * Parse latestPubDate
     *
     * @param configuration RSSConfiguration instance
     * @param feed Link to RSS feed
     * @param source parsed Map from FeedModel
     * @throws InvalidObjectException in case of feed is not configured, Item contains mandatory fields
     *              or pubDate is null by some reason
     */
    RSSItem(RSSConfiguration configuration, String feed, Map<String, String> source) throws InvalidObjectException {
        if (!configuration.getRSSFeeds().containsKey(feed)) {
            throw new InvalidObjectException("RSS Item is not configured in RSS Configuration");
        }

        if (! source.keySet().containsAll(RSSConfiguration.getRawMandatoryItemFields())) {
            throw new InvalidObjectException("RSS Item does not contains all the mandatory fields");
        }

        if (source.get("pubDate".toLowerCase()) == null) {
            throw new InvalidObjectException("RSS Item has PubDate set to null");
        }

        this.body = new HashMap<>();
        source.forEach((key, value) -> {
            if (configuration.getItemFields(feed).contains(key.toLowerCase())) {
                body.put(key, value);
            }
        });

        latestPubDate = PubDateParser.parse(source.get("pubDate".toLowerCase()));
    }
}
