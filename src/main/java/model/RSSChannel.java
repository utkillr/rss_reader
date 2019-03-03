package model;

import config.RSSConfiguration;

import java.io.InvalidObjectException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents RSS Channel (Atom syntax is ignored for now).
 */
public class RSSChannel {

    // Actually HashMap to support sorting
    private Map<String, String> metaBody;
    private List<RSSItem> items;
    private Date latestPubDate;

    /**
     * Get properties of RSS Channel
     *
     * @return map: Configured property -> value
     */
    public Map<String, String> getMetaBody() {
        return metaBody;
    }

    /**
     * Get list of items inside of channel
     *
     * @return list of items
     */
    public List<RSSItem> getItems() {
        return items;
    }

    /**
     * Get latest pub date, which is assigned in constructor.
     * Actually, the latest pubDate of items
     *
     * @return latest pubDate
     */
    public Date getLatestPubDate() {
        return latestPubDate;
    }

    /**
     * Setup configured channel fields
     * Setup items which are newer than latestPubDate
     * Update latestPubDate
     *
     * @param configuration RSSConfiguration instance
     * @param feed Link to RSS feed
     * @param model parsed FeedModel
     * @throws InvalidObjectException in case of feed is not configured or Channel contains mandatory fields
     */
    public RSSChannel(RSSConfiguration configuration, String feed, FeedModel model)
            throws InvalidObjectException {
        if (! configuration.getRSSFeeds().containsKey(feed)) {
            throw new InvalidObjectException("RSS Channel is not configured in RSS Configuration");
        }
        if (! model.metaSource.keySet().containsAll(RSSConfiguration.getRawMandatoryChannelFields())) {
            throw new InvalidObjectException("RSS Channel does not contains all the mandatory fields");
        }

        latestPubDate = RSSConfiguration.getInstance().getRSSFeedLastPubDate(feed);

        this.metaBody = new HashMap<>();
        model.metaSource.forEach((key, value) -> {
            if (configuration.getChannelFields(feed).contains(key.toLowerCase())) {
                metaBody.put(key, value);
            }
        });
        this.items = new ArrayList<>();
        for (Map<String, String> item : model.itemSources) {
            items.add(new RSSItem(configuration, feed, item));
        }

        items = items
                .stream()
                .filter(i -> i.getLatestPubDate().compareTo(latestPubDate == null ? new Date(0) : latestPubDate) > 0)
                .sorted(Comparator.comparing(RSSItem::getLatestPubDate, Comparator.reverseOrder()))
                .limit(configuration.getFeedMaxItems(feed))
                .collect(Collectors.toList());

        this.latestPubDate = items
                .stream()
                .map(RSSItem::getLatestPubDate)
                .max(Comparator.naturalOrder())
                .orElse(this.latestPubDate);
    }
}
