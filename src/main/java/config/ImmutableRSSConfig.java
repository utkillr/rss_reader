package config;

import model.FeedModel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class ImmutableRSSConfig {
    private static String ITEM_TITLE = "title";
    private static String ITEM_DESCRIPTION = "description";
    private static String ITEM_LINK = "link";
    private static String ITEM_AUTHOR = "author";
    private static String ITEM_CATEGORY = "category";
    private static String ITEM_COMMENTS = "comments";
    private static String ITEM_ENCLOSURE = "enclosure";
    private static String ITEM_GUID = "guid";
    private static String ITEM_PUB_DATE = "pubDate";
    private static String ITEM_SOURCE = "source";
    static List<String> availableItemFields = Arrays.asList(
            ITEM_TITLE, ITEM_DESCRIPTION, ITEM_LINK, ITEM_AUTHOR, ITEM_CATEGORY, ITEM_COMMENTS, ITEM_ENCLOSURE,
            ITEM_GUID, ITEM_PUB_DATE, ITEM_SOURCE

    );
    static List<String> rawAvailableItemFields = availableItemFields
            .stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    static List<String> defaultRawAvailableItemFields = Arrays.asList(ITEM_TITLE, ITEM_DESCRIPTION);
    static List<String> rawMandatoryItemFields = Arrays.asList(ITEM_TITLE, ITEM_PUB_DATE)
            .stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());

    private static String CHANNEL_TITLE = "title";
    private static String CHANNEL_LINK = "link";
    private static String CHANNEL_DESCRIPTION = "description";
    private static String CHANNEL_LANGUAGE = "language";
    private static String CHANNEL_COPYRIGHT = "copyright";
    private static String CHANNEL_MANAGING_EDITOR = "managingEditor";
    private static String CHANNEL_WEB_MASTER = "webMaster";
    private static String CHANNEL_PUB_DATE = "pubDate";
    private static String CHANNEL_LAST_BUILD_DATE = "lastBuildDate";
    private static String CHANNEL_CATEGORY = "category";
    private static String CHANNEL_GENERATOR = "generator";
    private static String CHANNEL_DOCS = "docs";
    private static String CHANNEL_CLOUD = "cloud";
    private static String CHANNEL_TTL = "ttl";
    private static String CHANNEL_RATING = "rating";
    private static String CHANNEL_TEXT_INPUT = "textInput";
    private static String CHANNEL_SKIP_HOURS = "skipHours";
    private static String CHANNEL_SKIP_DAYS = "skipDays";
    static List<String> availableChannelFields = Arrays.asList(
            CHANNEL_TITLE, CHANNEL_LINK, CHANNEL_DESCRIPTION, CHANNEL_LANGUAGE, CHANNEL_COPYRIGHT,
            CHANNEL_MANAGING_EDITOR, CHANNEL_WEB_MASTER, CHANNEL_PUB_DATE, CHANNEL_LAST_BUILD_DATE, CHANNEL_CATEGORY,
            CHANNEL_GENERATOR, CHANNEL_DOCS, CHANNEL_CLOUD, CHANNEL_TTL, CHANNEL_RATING, CHANNEL_TEXT_INPUT,
            CHANNEL_SKIP_HOURS, CHANNEL_SKIP_DAYS
    );
    static List<String> rawAvailableChannelFields = availableChannelFields
            .stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());
    static List<String> defaultRawAvailableChannelFields = Arrays.asList(CHANNEL_TITLE, CHANNEL_DESCRIPTION);
    static List<String> rawMandatoryChannelFields = Arrays.asList(CHANNEL_TITLE)
            .stream()
            .map(String::toLowerCase)
            .collect(Collectors.toList());

    /**
     * Special method which knows how atom fields correspond to RSS 2.0 fields
     * If field is already RSS, return it.
     * If field is unknown, return empty String.
     *
     * @param atomField name of Atom field with prefix
     * @return RSS field name
     */
    static String atomFieldToRSSField(String atomField) {
        if (!atomField.startsWith("atom")) return atomField;
        switch (atomField) {
            case "atom:author": return ITEM_AUTHOR.toLowerCase();
            case "atom:category": return CHANNEL_CATEGORY.toLowerCase();
            case "atom:feed": return FeedModel.FEED_CHANNEL.toLowerCase();
            case "atom:rights": return CHANNEL_COPYRIGHT.toLowerCase();
            // Ignored by RSS
            case "atom:subtitle": return "subtitle";
            case "atom:summary": return CHANNEL_DESCRIPTION.toLowerCase();
            case "atom:content": return ITEM_DESCRIPTION.toLowerCase();
            case "atom:generator": return CHANNEL_GENERATOR.toLowerCase();
            case "atom:id": return  ITEM_GUID.toLowerCase();
            // Look in specification
            case "atom:logo": return "image";
            case "atom:entry": return FeedModel.FEED_ITEM.toLowerCase();
            case "atom:updated": return CHANNEL_PUB_DATE.toLowerCase();
            case "atom:link": return CHANNEL_LINK.toLowerCase();
            case "atom:contributor": return CHANNEL_MANAGING_EDITOR.toLowerCase();
            case "atom:published": return ITEM_PUB_DATE.toLowerCase();
            case "atom:title": return CHANNEL_TITLE.toLowerCase();
            default: return "";
        }
    }
}
