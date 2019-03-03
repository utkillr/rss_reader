package cli;

import javax.xml.bind.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Parser for CLI Wrapper
 */
public class CommandLineParser {
    private CommandLineManager manager;

    /**
     * Default constructor with default manager
     */
    public CommandLineParser() {
        manager = new CommandLineManager();
    }

    /**
     *
     * @param manager
     */
    public CommandLineParser(CommandLineManager manager) {
        this.manager = manager;
    }

    /**
     * Parse input command (cli wrapper)
     *
     * @param inputString full command containing of command and its parameters
     * @return execution status:
     *      0 means no errors
     *      1 means graceful exit
     * @throws ValidationException in case of invalid feed
     * @throws IllegalArgumentException in case of line can't be parsed
     */
    public int parse(String inputString) throws ValidationException, IllegalArgumentException {
        String cmd = null;
        List<String> params = null;
        List<String> blocks = Arrays.asList(inputString.split(" "));
        blocks = blocks.stream().map(String::trim).filter(block -> !block.isEmpty()).collect(Collectors.toList());
        if (!blocks.isEmpty()) {
            cmd = blocks.get(0);
            params = blocks.size() > 1 ? blocks.subList(1, blocks.size()) : Collections.EMPTY_LIST;
        }

        // rss
        if ("rss".equals(cmd)) {
            // go into
            if (params.size() > 0) {
                String subCommand = params.get(0);
                params = params.subList(1, params.size());
                switch (subCommand) {
                    // addition
                    case "add": {
                        if (params.size() < 2) {
                            throw new IllegalArgumentException("Not enough params for rss add");
                        }
                        if (params.size() > 3) {
                            throw new IllegalArgumentException("Too many params for rss add");
                        }
                        String rssLink = params.get(0);
                        String file = params.get(1);
                        manager.createFileIfNotExists(file);
                        manager.associateRssToFile(rssLink, file);
                        manager.prettyPrint(
                                "Successfully added new RSS Feed: " + rssLink,
                                "Associated file is: " + file
                        );
                        if (params.size() == 3) {
                            manager.setRSSMaxItems(rssLink, Integer.valueOf(params.get(2)));
                            manager.prettyPrint("Set maxCount to " + params.get(2));
                        }
                        break;
                    }
                    // removal
                    case "del": {
                        if (params.size() < 1) {
                            throw new IllegalArgumentException("Not enough params for rss del");
                        }
                        for (String rssLink : params) {
                            manager.dissociateRss(rssLink);
                            manager.prettyPrint("Successfully removed new RSS Feed: " + rssLink);
                        }
                        break;
                    }
                    // turn on
                    case "on": {
                        if (params.size() < 1) {
                            throw new IllegalArgumentException("Not enough params for rss on");
                        }
                        for (String rssLink : params) {
                            manager.turnRSSOn(rssLink);
                            manager.prettyPrint("Successfully turned RSS Feed " + rssLink + " on");
                        }
                        break;
                    }
                    // turn off
                    case "off": {
                        if (params.size() < 1) {
                            throw new IllegalArgumentException("Not enough params for rss off");
                        }
                        for (String rssLink : params) {
                            manager.turnRSSOff(rssLink);
                            manager.prettyPrint("Successfully turned RSS Feed " + rssLink + " off");
                        }
                        break;
                    }
                    case "file": {
                        if (params.size() < 1) {
                            throw new IllegalArgumentException("Not enough params for rss file");
                        } else if (params.size() > 2) {
                            throw new IllegalArgumentException("Too many params for rss file");
                        }
                        String rssLink = params.get(0);
                        // print current file
                        if (params.size() == 1) {
                            manager.printRssFile(rssLink);
                        } else if (params.size() == 2) {
                            manager.reassociateRssToFile(rssLink, params.get(1));
                        }
                    }
                    // go into item fields
                    case "item": {
                        // print available
                        if (params.size() == 0) {
                            manager.printAvailableRssItemParams();
                        } else {
                            String rssLink = params.get(0);
                            params = params.subList(1, params.size());
                            // print configured
                            if (params.size() == 0) {
                                manager.printRssItemParams(rssLink);
                            // set
                            } else {
                                manager.setRssItemParams(rssLink, params);
                                manager.prettyPrint("Successfully set Item Properties to: " + params);
                            }
                        }
                        break;
                    }
                    // go into channel fields
                    case "channel": {
                        // print available
                        if (params.size() == 0) {
                            manager.printAvailableRssChannelParams();
                        } else {
                            String rssLink = params.get(0);
                            params = params.subList(1, params.size());
                            // print configured
                            if (params.size() == 0) {
                                manager.printRssChannelParams(rssLink);
                            // set
                            } else {
                                manager.setRssChannelParams(rssLink, params);
                                manager.prettyPrint("Successfully set Channel Properties to: " + params);
                            }
                        }
                        break;
                    }
                    // go into max
                    case "max": {
                        if (params.size() < 1) {
                            throw new IllegalArgumentException("Not enough params for rss file");
                        } else if (params.size() > 2) {
                            throw new IllegalArgumentException("Too many params for rss file");
                        }
                        String rssLink = params.get(0);
                        // print current max
                        if (params.size() == 1) {
                            manager.printRSSMaxItems(rssLink);
                        } else if (params.size() == 2) {
                            manager.setRSSMaxItems(rssLink, Integer.valueOf(params.get(1)));
                        }
                        break;
                    }
                }
            // print all feeds
            } else {
                manager.printRss();
            }
        // time
        } else if ("time".equals(cmd)) {
            // set
            if (params.size() > 0) {
                if (params.size() != 1) {
                    throw new IllegalArgumentException("Not enough params for -time");
                }
                String newTimeToPoll = params.get(0);
                manager.setTimeToPoll(Long.valueOf(newTimeToPoll));
                manager.prettyPrint("Successfully set polling time to " + newTimeToPoll);
            // print
            } else {
                manager.printTimeToPoll();
            }
        // help
        } else if ("help".equals(cmd)) {
            manager.printHelp();
        // save
        } else if ("save".equals(cmd)) {
            manager.saveConfiguration();
        // exit
        } else if ("exit".equals(cmd)) {
            manager.prettyPrint("Exiting...");
            return 1;
        } else if (cmd != null && !cmd.isEmpty()) {
            throw new IllegalArgumentException("Unknown argument: " +  cmd + ". Try help");
        }
        return 0;
    }
}
