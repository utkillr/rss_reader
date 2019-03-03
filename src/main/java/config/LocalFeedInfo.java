package config;

import java.util.Date;

/**
 * Aggregator class for FeedStatus and last PubDate
 */
class LocalFeedInfo {
    FeedStatus status;
    Date lastPubDate;

    /**
     * Default constructor. Feed is ON and pubDate is null.
     */
    LocalFeedInfo() {
        status = FeedStatus.ON;
        lastPubDate = null;
    }

    /**
     * Constructor with status and pubDate
     *
     * @param status status of Feed
     * @param pubDate last pub date of Feed
     */
    public LocalFeedInfo(String status, long pubDate) {
        this.status = status.equals("ON") ? FeedStatus.ON : FeedStatus.OFF;
        this.lastPubDate = new Date(pubDate);
    }
}
