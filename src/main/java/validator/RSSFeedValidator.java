package validator;

import config.RSSConfiguration;
import model.FeedModel;
import parser.FeedModelParser;
import util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * This class implements simple validation logic over RSS Feed, such as:
 *  Ability to parse and form
 *  Containment of mandatory fields
 */
public class RSSFeedValidator {

    private static Log log = new Log(RSSFeedValidator.class.getName(), System.out);

    /**
     * Validate feed.
     * Get its stream by link, parse.
     * Turn Feed into RSS.
     * Then check all the mandatory fields to be presented
     *
     * @param feed link to RSSFeed to validate
     * @return true if RSS Feed is valid, false otherwise
     */
    public boolean validate(String feed) {
        FeedModel model;
        try {
            InputStream in = new URL(feed).openStream();
            model = new FeedModelParser().parse(in);
        } catch (MalformedURLException e) {
            log.error("URL is malformed: " + e.getMessage());
            return false;
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }

        if (model.metaSource.isEmpty()) {
            return false;
        }

        model.atomToRSS();

        for (String key : RSSConfiguration.getRawMandatoryChannelFields()) {
            if (!model.metaSource.containsKey(key)) {
                return false;
            }
        }

        for (Map<String, String> itemSource : model.itemSources) {
            for (String key : RSSConfiguration.getRawMandatoryItemFields()) {
                if (!itemSource.containsKey(key)) {
                    return false;
                }
            }
        }

        return true;
    }
}
