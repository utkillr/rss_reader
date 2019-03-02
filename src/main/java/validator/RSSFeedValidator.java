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

public class RSSFeedValidator {

    private Log log = new Log("Validator", System.out);

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
