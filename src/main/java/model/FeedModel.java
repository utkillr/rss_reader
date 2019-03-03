package model;

import config.RSSConfiguration;
import lombok.EqualsAndHashCode;

import java.util.*;

/**
 * This class implements our model of RSS Feed which is actually
 * Channel description source (metaSource) + item description sources (itemSources)
 */
@EqualsAndHashCode
public class FeedModel {

    public static String FEED_ITEM = "item";
    public static String ATOM_ITEM = "entry";
    public static String FEED_CHANNEL ="channel";
    public static String ATOM_CHANNEL ="feed";

    public Map<String, String> metaSource;
    public List<Map<String, String>> itemSources;

    /**
     * Default constructor
     */
    public FeedModel() {
        metaSource = new HashMap<>();
        itemSources = new ArrayList<>();
    }

    /**
     * Special method to create new FeedModel with only RSS fields
     */
    public void atomToRSS() {
        Map<String, String> newMetaSource = new HashMap<>();
        metaSource.keySet().forEach(key -> putAtomOrRss(metaSource, newMetaSource, key));
        List<Map<String, String>> newItemSources = new ArrayList<>();
        for (Map<String, String> itemSource : itemSources) {
            Map<String, String> newItemSource = new HashMap<>();
            itemSource.keySet().forEach(key -> putAtomOrRss(itemSource, newItemSource, key));
            newItemSources.add(newItemSource);
        }
        this.metaSource = newMetaSource;
        this.itemSources = newItemSources;
    }

    /**
     * Helper method to put the same value, but RSS feed from src to dest
     *
     * @param src Source Map with Atom fields
     * @param dest Destination map with RSS fields only
     * @param key key to get and put
     */
    private void putAtomOrRss(Map<String, String> src, Map<String, String> dest, String key) {
        String transformedKey = RSSConfiguration.atomToRSS(key);
        // RSS keys are in priority
        if (transformedKey.equals(key)) {
            dest.put(transformedKey, src.get(key));
        // put Atom only if it's not put
        } else if (!dest.containsKey(transformedKey)){
            dest.put(transformedKey, src.get(key));
        }
    }


}
