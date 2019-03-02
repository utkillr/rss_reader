package config;

import java.util.Date;

/**
 * Aggregator class for FeedStatus and last PubDate
 */
class LocalFeedInfo {
    FeedStatus status;
    Date lastPubDate;

    LocalFeedInfo() {
        status = FeedStatus.ON;
        lastPubDate = null;
    }

    public LocalFeedInfo(String status, long pubDate) {
        this.status = status.equals("ON") ? FeedStatus.ON : FeedStatus.OFF;
        this.lastPubDate = new Date(pubDate);
    }
}
