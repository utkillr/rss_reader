package cli;

import config.AutoRSSConfigurator;
import config.RSSConfiguration;
import util.Log;
import validator.RSSFeedValidator;

import javax.xml.bind.ValidationException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
* Implementation of CLI
*/
class CommandLineManager {

    private static Log log = new Log(CommandLineManager.class.getName(), System.out);

    private RSSFeedValidator validator;

    /**
     * Default constructor with default validator
     */
    public CommandLineManager() {
        this.validator = new RSSFeedValidator();
    }

    /**
     * Constructor with external validator
     *
     * @param validator instance of RSSFeedValidator
     */
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
                    "save:\n\t\t" +
                        "Save current configuration\n\t" +
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
     * @throws ValidationException in case of link is invalid
     */
    void associateRssToFile(String link, String file) throws ValidationException {
        if (validator.validate(link)) {
            RSSConfiguration.getInstance().addRSSFeed(link, file);
            log.info(link + " is associated to " + file);
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
        log.info(link + " is dissociated");
    }

    /**
     * Reassociate RSS with file
     *
     * @param link rss feed link
     * @param file file name
     * @throws ValidationException in case of link is invalid
     */
    void reassociateRssToFile(String link, String file) throws ValidationException {
        if (validator.validate(link)) {
            RSSConfiguration.getInstance().setRSSFeedFile(link, file);
            log.info(link + " is reassociated to " + file);
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
        prettyPrint(link + " is associated to " + RSSConfiguration.getInstance().getRSSFeeds().get(link));
    }

    /**
     * Turn RSS feed On
     *
     * @param link rss feed link
     */
    void turnRSSOn(String link) {
        RSSConfiguration.getInstance().turnOnRSSFeed(link);
        log.info(link + " is on");
    }

    /**
     * Turn RSS feed Off
     *
     * @param link rss feed link
     */
    void turnRSSOff(String link) {
        RSSConfiguration.getInstance().turnOffRSSFeed(link);
        log.info(link + " is off");
    }

    /**
     * Set RSS feed max items
     *
     * @param link rss feed link
     * @param maxItems new max count of items
     */
    void setRSSMaxItems(String link, Integer maxItems) {
        RSSConfiguration.getInstance().setFeedMaxItems(link, maxItems);
        log.info("Set maxItems of " + link + " to " + maxItems);
    }

    void printRSSMaxItems(String link) {
        prettyPrint("Configured maxItems count for " + link + " is " + RSSConfiguration.getInstance().getFeedMaxItems(link));
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
        List<String> res = new ArrayList<>();
        Map<String, String> rssFeeds = RSSConfiguration.getInstance().getRSSFeeds();
        res.add("RSS Feeds are:");
        if (rssFeeds.size() > 0) {
            rssFeeds.forEach((rss, file) ->
                    res.add(
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
        prettyPrint(res.toArray());
    }

    /**
     * Get list of available item params
     */
    void printAvailableRssItemParams() {
        prettyPrint("Available item params:",
                RSSConfiguration.getAvailableItemFields().toString());
    }

    /**
     * Get list of configured item params
     *
     * @param feed RSS Feed to get params
     */
    void printRssItemParams(String feed) {
        prettyPrint("Configured item params for " + feed + ":",
                RSSConfiguration.getInstance().getItemFields(feed).toString());
    }

    /**
     * Set list of used item params
     *
     * @param feed RSS Feed to set params
     * @param params params to print
     */
    void setRssItemParams(String feed, List<String> params) {
        RSSConfiguration.getInstance().reconfig(feed, params, Collections.emptyList());
        log.info("Set " + feed + " item params to " + params);
    }

    /**
     * Get list of available channel params
     */
    void printAvailableRssChannelParams() {
        prettyPrint("Available channel params:",
                RSSConfiguration.getAvailableChannelFields().toString());
    }

    /**
     * Get list of configured channel params
     *
     * @param feed RSS Feed to set params
     */
    void printRssChannelParams(String feed) {
        prettyPrint("Configured channel params for " + feed + ":",
                RSSConfiguration.getInstance().getChannelFields(feed).toString());
    }

    /**
     * Set list of used channel params
     *
     * @param feed RSS Feed to set params
     * @param params params to print
     */
    void setRssChannelParams(String feed, List<String> params) {
        RSSConfiguration.getInstance().reconfig(feed, Collections.emptyList(), params);
        log.info("Set " + feed + " channel params to " + params);
    }

    /**
     * Set time to poll RSS Feeds
     *
     * @param time time in seconds
     */
    void setTimeToPoll(Long time) {
        RSSConfiguration.getInstance().setTimeToPoll(time);
        log.info("Set time to poll to " + time);
    }

    /**
     * Print time to poll RSS Feeds
     */
    void printTimeToPoll() {
        prettyPrint("Time to poll is " + RSSConfiguration.getInstance().getTimeToPoll());
    }

    /**
     * Save current configurations
     */
    void saveConfiguration() {
        AutoRSSConfigurator.saveRSSConfiguration();
        log.info("Configuration saved");
    }

    /**
     * Pretty format of cli response
     *
     * @param messages Strings to output line by line
     */
    void prettyPrint(Object... messages) {
        for (Object message : messages) {
            log.response(message.toString());
        }
    }
}
