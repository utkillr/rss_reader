package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Util class for pubDate field parsing
 */
public class PubDateParser {

    /**
     * List of available date formats
     */
    private static DateFormat[] formatters = {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz"),
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm zzz"),
            new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy zzz"),
            new SimpleDateFormat("EEE MMM dd HH:mm yyyy zzz"),
            new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy"),
            new SimpleDateFormat("EEE MMM dd HH:mm zzz yyyy")
    };

    /**
     * Parse pubDate.
     * If not any of known formatters can parse the String, return null
     *
     * @param pubDate String representation of pubDate
     * @return Date representation of pubDate or null
     */
    public static Date parse(String pubDate) {
        for (DateFormat formatter : formatters) {
            try {
                return formatter.parse(pubDate);
            } catch (ParseException ignored) {}
        }
        return null;
    }
}
