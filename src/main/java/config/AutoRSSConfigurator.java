package config;

import util.Log;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

import static java.nio.file.StandardOpenOption.APPEND;

/**
 * Saves and loads configuration file with the following structure:
 * Time to poll
 * Feed;File;Status;LastPubDate;ChannelFields(,);ItemFields(,)
 * ...
 */
public class AutoRSSConfigurator {

    private static String file = "rss.cfg";
    private static Log log = new Log(AutoRSSConfigurator.class.getName(), System.out);

    private static boolean configurationSavedOnShutdown = false;

    public static void setFile(String file) {
        AutoRSSConfigurator.file = file;
    }

    public static void saveRSSConfigurationOnShutdown() {
        if (!configurationSavedOnShutdown) {
            saveRSSConfiguration();
            configurationSavedOnShutdown = true;
        }
    }

    public static void saveRSSConfiguration() {
        File f = new File(file);
        try {
            f.delete();
            f.createNewFile();
        } catch (IOException e) {
            log.error("Error occurred during creating file: " + e.getMessage());
            log.error("Configuration won't be saved");
            return;
        }

        synchronized (RSSConfiguration.getInstance()) {
            RSSConfiguration configuration = RSSConfiguration.getInstance();
            try {
                Files.write(f.toPath(), (getTimeToPoll(configuration) + "\n").getBytes(), APPEND);
                for (String feed : configuration.getRSSFeeds().keySet()) {
                    Files.write(f.toPath(), (getRSSFeedFullInfo(feed, configuration) + "\n").getBytes(), APPEND);
                }
            } catch (IOException e) {
                log.error("Not all the configuration can be written.");
            }
        }
    }

    public static void loadRSSConfiguration() {
        File f = new File(file);
        if (!f.canRead()) {
            log.error("Can't read config file. Configuration won't be loaded");
            return;
        }

        RSSConfiguration configuration = RSSConfiguration.getInstance();
        try {
            List<String> configList = Files.readAllLines(f.toPath());
            if (configList.size() < 1) {
                log.error("Config is invalid");
            } else {
                try {
                    long timeToPoll = Long.valueOf(configList.get(0));
                    configuration.setTimeToPoll(timeToPoll);
                } catch (NumberFormatException e) {
                    log.error("Set default time to poll - Can't parse value: " + configList.get(0));
                    configuration.setTimeToPoll(RSSConfiguration.defaultTimeToPoll);
                }
                if (configList.size() > 1) {
                    for (String line : configList.subList(1, configList.size())) {
                        if (line.isEmpty()) continue;
                        // Gonna be List of 6 elems: feed, link, status, pubdate, channel fields, item fields
                        List<String> parsed = parseParams(line);
                        if (parsed.size() != 6) {
                            log.warn("RSS Feed Configuration can't be read");
                            continue;
                        }
                        // Set feed and link
                        configuration.addRSSFeed(parsed.get(0), parsed.get(1));
                        // Set status
                        if (parsed.get(2).equals("0")) {
                            configuration.turnOffRSSFeed(parsed.get(0));
                        } else {
                            configuration.turnOnRSSFeed(parsed.get(0));
                        }
                        // Set pub date
                        try {
                            Date pubDate = new Date(Long.valueOf(parsed.get(3)));
                            configuration.notifyFeedRead(parsed.get(0), pubDate);
                        } catch (NumberFormatException e) {
                            log.error("Set last pubTime to NULL - Can't parse Long: " + parsed.get(3));
                            configuration.notifyFeedRead(parsed.get(0), null);
                        }
                        // Parse and set channel and item fields
                        List<String> channelFields = parseFields(parsed.get(4));
                        List<String> itemFields = parseFields(parsed.get(5));
                        configuration.reconfig(parsed.get(0), itemFields, channelFields);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Not all the configuration can be written.");
        }
    }

    private static String getTimeToPoll(RSSConfiguration configuration) {
        return configuration.getTimeToPoll().toString();
    }

    private static String getRSSFeedFullInfo(String feed, RSSConfiguration configuration) {
        StringBuilder builder = new StringBuilder();
        String lastPubDate = configuration.getRSSFeedLastPubDate(feed) == null
                        ? "null"
                        : Long.toString(configuration.getRSSFeedLastPubDate(feed).getTime());
        builder
                .append(feed).append(";")
                .append(configuration.getRSSFeeds().get(feed)).append(";")
                .append(configuration.isRSSFeedOn(feed) ? "1" : "0").append(";")
                .append(lastPubDate).append(";")
                .append(getFields(configuration.getChannelFields(feed))).append(";")
                .append(getFields(configuration.getItemFields(feed)));
        return builder.toString();
    }

    private static String getFields(List<String> fields) {
        StringJoiner joiner = new StringJoiner(",");
        for (String field : fields) {
            joiner.add(field);
        }
        return joiner.toString();
    }

    private static List<String> parseParams(String line) {
        return Arrays.asList(line.split(";"));
    }

    private static List<String> parseFields(String line) {
        return Arrays.asList(line.split(","));
    }
}
