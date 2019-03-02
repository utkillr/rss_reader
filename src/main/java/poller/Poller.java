package poller;

import config.RSSConfiguration;
import model.FeedModel;
import model.RSSChannel;
import model.RSSItem;
import parser.FeedModelParser;
import util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.APPEND;

/**
 * Runnable class to poll RSS Feeds and write them to associated files
 * with provided polling time
 */
public class Poller implements Runnable {
    private static Log log = new Log(Poller.class.getName(), System.out);

    /**
     * Time to sleep between checks for time to poll modifications
     */

    private boolean running = true;

    /**
     * Polling function.
     * Actually, iterates over all the feeds which are turned on and prints them to file.
     * Also notifies configurator about new pubDate
     *
     * @param configuration instance of RSSConfiguration
     */
    void poll(RSSConfiguration configuration) {
        configuration.getRSSFeeds().forEach((feed, file) -> {
            if (configuration.isRSSFeedOn(feed)) {
                try {
                    InputStream in = new URL(feed).openStream();
                    Date newPubDate = handleRSSFeed(in, feed, file);
                    configuration.notifyFeedRead(feed, newPubDate);
                } catch (IllegalArgumentException e) {
                    log.error(e.getMessage());
                } catch (MalformedURLException e) {
                    log.error("Can't read URL. " + e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Handle and Print RSS Feed to the file.
     * In case pubDates are available, filter RSS Items to be newer than latestPubDate.
     * In case no new items arrived, do not write anything.
     *
     * @param link        rss feed link
     * @param file        file name
     * @return updated latestPubDate
     */
    Date handleRSSFeed(InputStream in, String link, String file) {
        try {
            FeedModel model = new FeedModelParser().parse(in);
            // TODO: model.atomToRSS();
            model.atomToRSS();
            Path path = Paths.get(file);
            RSSChannel channel = new RSSChannel(RSSConfiguration.getInstance(), link, model);
            printRSSFeedToFile(channel, link, path);
            return channel.getLatestPubDate();
        } catch (IOException e) {
            log.error("Error occurred during writing RSS Feed to the file: " + e.getMessage());
        }

        return null;
    }

    /**
     * Print RSS Feed to the file.
     *
     * @param link        rss feed link
     * @param path        path to file
     * @return updated latestPubDate
     */
    void printRSSFeedToFile(RSSChannel channel, String link, Path path) throws IOException {
        // we do not want to append empty channel description
        if (channel.getItems().size() > 0) {

            final String feedString = getStringFromMap(
                    channel.getMetaBody(), RSSConfiguration.getInstance().getChannelFields(link), 0
            );
            File file = path.toFile();
            if (!file.exists() && !file.isDirectory()) {
                file.createNewFile();
            }
            Files.write(path, feedString.getBytes(), APPEND);

            for (RSSItem item : channel.getItems()) {
                final String itemString = getStringFromMap(
                        item.getBody(), RSSConfiguration.getInstance().getItemFields(link), 1
                );
                Files.write(path, itemString.getBytes(), APPEND);
            }
        }
    }

    /**
     * Helper method for getting user-friendly string from channel or item properties map
     *
     * @param map           properties map
     * @param availableKeys keys to include
     * @param initialIndent initial indent for all the lines
     * @return User-friendly string ready for output
     */
    static String getStringFromMap(Map<String, String> map, List<String> availableKeys, int initialIndent) {
        StringBuilder indentation = new StringBuilder();
        for (int i = 0; i < initialIndent; i++) {
            indentation.append("\t");
        }
        String indentationStr = indentation.toString();
        return map.entrySet()
                .stream()
                .filter((keyValue) -> availableKeys.contains(keyValue.getKey()))
                .map(keyValue -> String.format("%s%s:\n%s\t%s\n", indentationStr, keyValue.getKey(),
                        indentationStr, keyValue.getValue()))
                .collect(Collectors.joining()) + "\n";
    }

    /**
     * Overriding of Runnable.run.
     * Unless stop() is not called, poll and sleep in loop
     */
    @Override
    public void run() {
        RSSConfiguration configuration = RSSConfiguration.getInstance();
        while (running) {
            poll(configuration);
            try {
                sleep(configuration);
            } catch (InterruptedException e) {
                log.error("Thread is interrupted during sleep: " + e.getMessage());
            }
        }
    }

    /**
     * Set 'running' to false which causes stopping of run() loop
     */
    public void stop() {
        running = false;
    }

    /**
     * Special sleep method which allows to modify and reapply configured time to poll.
     * Check if time to poll is changed every @timeCheckThreshold seconds.
     * If it's changed, reapply it locally and restart sleeping cycle.
     * In case of stop() has been called , stop sleeping on next waking up.
     *
     * @param configuration instance of RSSConfiguration
     * @throws InterruptedException in case of interrupted sleep
     */
    private void sleep(RSSConfiguration configuration) throws InterruptedException {
        long currentTimeToPoll = configuration.getTimeToPoll();
        long leftTimeToPoll = currentTimeToPoll;
        while (leftTimeToPoll > RSSConfiguration.timeCheckThreshold) {
            long newTimeToPoll = configuration.getTimeToPoll();
            // If somebody changed poll time, apply it and start sleeping from scratch
            if (newTimeToPoll != currentTimeToPoll) {
                currentTimeToPoll = newTimeToPoll;
                leftTimeToPoll = currentTimeToPoll;
            }
            if (running) {
                TimeUnit.SECONDS.sleep(RSSConfiguration.timeCheckThreshold);
            }
            leftTimeToPoll -= RSSConfiguration.timeCheckThreshold;
        }
        if (running) {
            TimeUnit.SECONDS.sleep(leftTimeToPoll);
        }
    }
}
