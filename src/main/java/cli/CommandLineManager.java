package cli;

import config.RSSConfiguration;
import util.Log;
import validator.RSSFeedValidator;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

  /**
 * Implementation of CLI
 */
class CommandLineManager {

    private static Log log = new Log(CommandLineManager.class.getName(), System.out);

    private RSSFeedValidator validator;

    public CommandLineManager() {
        this.validator = new RSSFeedValidator();
    }

    public CommandLineManager(RSSFeedValidator validator) {
        this.validator = validator;
    }

    /**
     * Print information about possible commands to the console
     */
    void printHelp() {
        final PrintWriter writer = new PrintWriter(System.out);
        final String helpMessage =
                "Available options:\n\t" +
                    "rss:\n\t\t" +
                        "(w/o params):\n\t\t\t" +
                            "Get list of RSS Feeds and associated files\n\t\t" +
                        "add <rss link> <file> OR add <rss link> <file> <count>:\n\t\t\t" +
                            "Link RSS Feed to the file. If count is provided, set max items count per poll\n\t\t\t" +
                            "If file exists or is a directory, error is raised\n\t\t" +
                        "del <rss link> ... :\n\t\t\t" +
                            "Delete RSS Feeds and associated files\n\t\t" +
                        "on <rss link> ... :\n\t\t\t" +
                            "Turn RSS Feeds ON\n\t\t" +
                        "off <rss link> ... :\n\t\t\t" +
                            "Turn RSS Feeds OFF\n\t\t" +
                        "channel:\n\t\t\t" +
                            "(w/o params):\n\t\t\t\t" +
                                "Get list of available legal channel fields\n\t\t\t" +
                            "<rss link>:\n\t\t\t\t" +
                                "Get list of channel fields configured for RSS Feed\n\t\t\t" +
                            "<rss link> <property_1> ... <property_n>:\n\t\t\t\t" +
                                "Configure list of channel fields for RSS Feed\n\t\t" +
                        "item:\n\t\t\t" +
                            "(w/o params):\n\t\t\t\t" +
                                "Get list of available legal item fields\n\t\t\t" +
                            "<rss link>:\n\t\t\t\t" +
                                "Get list of item fields configured for RSS Feed\n\t\t\t" +
                            "<rss link> <property_1> ... <property_n>:\n\t\t\t\t" +
                                "Configure list of item fields for RSS Feed\n\t\t" +
                        "max:\n\t\t\t" +
                            "<rss link>:\n\t\t\t\t" +
                                "Get max count of items per poll for RSS Feed\n\t\t\t" +
                            "<rss link> <count>:\n\t\t\t\t" +
                                "Set max count of items per poll for RSS Feed\n\t" +
                    "time:\n\t\t" +
                        "(w/o params):\n\t\t\t" +
                            "Get current time to poll in seconds\n\t\t" +
                        "<time>:\n\t\t\t" +
                            "Set time to poll in seconds\n\t" +
                    "help:\n\t\t" +
                        "Print this Help message\n\t" +
                    "exit:\n\t\t" +
                        "Exit from application";
        writer.println(helpMessage);
        writer.flush(); // вывод
    }

    /**
     * Create file if it does not exist yet
     *
     * @param file file name
     */
    void createFileIfNotExists(String file) {
        File f = new File(file);
        if (!f.exists() && !f.isDirectory()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                log.error("Error occurred during creating file: " + e.getMessage());
            }
        }
    }

    /**
     * Associate RSS with file
     *
     * @param link rss feed link
     * @param file file name
     */
    void associateRssToFile(String link, String file) throws ValidationException {
        if (validator.validate(link)) {
            RSSConfiguration.getInstance().addRSSFeed(link, file);
        } else {
            throw new ValidationException("RSS Feed " + link + " is invalid");
        }
    }

    /**
     * Disassociate RSS with file
     *
     * @param link rss feed link
     */
    void dissociateRss(String link) {
        RSSConfiguration.getInstance().delRSSFeed(link);
    }

    /**
     * Reassociate RSS with file
     *
     * @param link rss feed link
     * @param file file name
     */
    void reassociateRssToFile(String link, String file) throws ValidationException {
        if (validator.validate(link)) {
            RSSConfiguration.getInstance().setRSSFeedFile(link, file);
        } else {
            throw new ValidationException("RSS Feed " + link + " is invalid");
        }
    }

    /**
     * Get RSS output file
     *
     * @param link rss feed link
     */
    void printRssFile(String link) {
        System.out.println("Associated file: " + RSSConfiguration.getInstance().getRSSFeeds().get(link));
    }

    /**
     * Turn RSS feed On
     *
     * @param link rss feed link
     */
    void turnRSSOn(String link) {
        RSSConfiguration.getInstance().turnOnRSSFeed(link);
    }

    /**
     * Turn RSS feed Off
     *
     * @param link rss feed link
     */
    void turnRSSOff(String link) {
        RSSConfiguration.getInstance().turnOffRSSFeed(link);
    }

      /**
       * Set RSS feed max items
       *
       * @param link rss feed link
       * @param maxItems new max count of items
       */
    void setRSSMaxItems(String link, Integer maxItems) {
        RSSConfiguration.getInstance().setFeedMaxItems(link, maxItems);
    }

    void printRSSMaxItems(String link) {
        System.out.println("Configured max items count:\n" + RSSConfiguration.getInstance().getFeedMaxItems(link));
    }

    /**
     * Get Rss status (On or Off)
     *
     * @param link rss feed link
     */
    private String getRssStatus(String link) {
        return RSSConfiguration.getInstance().isRSSFeedOn(link) ? "ON" : "OFF";
    }

    /**
     * Print all the RSS feeds with associated files
     */
    void printRss() {
        Map<String, String> rssFeeds = RSSConfiguration.getInstance().getRSSFeeds();
        final PrintWriter writer = new PrintWriter(System.out);
        if (rssFeeds.size() > 0) {
            rssFeeds.forEach((rss, file) ->
                    writer.println(
                            String.format(
                                    "%s (%s): %s",
                                    rss,
                                    getRssStatus(rss),
                                    file
                            )
                    )
            );
        } else {
            log.warn("No RSS available");
        }
        writer.flush(); // вывод
    }

    /**
     * Get list of available item params
     */
    void printAvailableRssItemParams() {
        System.out.println("Available item params:\n" + RSSConfiguration.getAvailableItemFields());
    }

    /**
     * Get list of configured item params
     *
     * @param feed RSS Feed to get params
     */
    void printRssItemParams(String feed) {
        System.out.println("Configured item params:\n" + RSSConfiguration.getInstance().getItemFields(feed));
    }

    /**
     * Set list of used item params
     *
     * @param feed RSS Feed to set params
     * @param params params to print
     */
    void setRssItemParams(String feed, List<String> params) {
        RSSConfiguration.getInstance().reconfig(feed, params, Collections.emptyList());
    }

    /**
     * Get list of available channel params
     */
    void printAvailableRssChannelParams() {
        System.out.println("Available channel params:\n" + RSSConfiguration.getAvailableChannelFields());
    }

    /**
     * Get list of configured channel params
     *
     * @param feed RSS Feed to set params
     */
    void printRssChannelParams(String feed) {
        System.out.println("Configured channel params:\n" + RSSConfiguration.getInstance().getChannelFields(feed));
    }

    /**
     * Set list of used channel params
     *
     * @param feed RSS Feed to set params
     * @param params params to print
     */
    void setRssChannelParams(String feed, List<String> params) {
        RSSConfiguration.getInstance().reconfig(feed, Collections.emptyList(), params);
    }

    /**
     * Set time to poll RSS Feeds
     *
     * @param time time in seconds
     */
    void setTimeToPoll(Long time) {
        RSSConfiguration.getInstance().setTimeToPoll(time);
    }

    /**
     * Print time to poll RSS Feeds
     */
    void printTimeToPoll() {
        long timeToPoll = RSSConfiguration.getInstance().getTimeToPoll();
        final PrintWriter writer = new PrintWriter(System.out);
        writer.println(timeToPoll);
        writer.flush(); // вывод
    }

    /**
     * Pretty format of cli response
     * @param messages Strings to output line by line
     */
    void prettyPrint(String... messages) {
        for (String message : messages) {
            log.response(message);
        }
        log.emptyLine();

    }
}
