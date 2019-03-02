package model;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FeedModelTest {
    @Test
    @DisplayName("Test to turn Atom into RSS")
    public void atomToRssTest() {
        FeedModel model = new FeedModel();
        model.metaSource.put("atom:title", "dummy title");
        model.metaSource.put("atom:id", "dummy guid");
        model.metaSource.put("atom:contributor", "dummy managingeditor");

        Map<String, String> itemSource = new HashMap<>();
        itemSource.put("atom:rights", "dummy copyright");
        itemSource.put("atom:published", "dummy pubdate");
        itemSource.put("atom:content", "dummy description");
        model.itemSources.add(itemSource);

        model.atomToRSS();

        assertEquals(3, model.metaSource.size());
        for (String key : model.metaSource.keySet()) {
            assertEquals("dummy " + key, model.metaSource.get(key));
        }
        assertEquals(1, model.itemSources.size());
        assertEquals(3, model.itemSources.get(0).size());
        for (String key : model.itemSources.get(0).keySet()) {
            assertEquals("dummy " + key, model.itemSources.get(0).get(key));
        }
    }

    @Test
    @DisplayName("Test to turn Atom into RSS when there is not Atom")
    public void atomToRssNoAtomTest() {
        FeedModel model = new FeedModel();
        model.metaSource.put("title", "dummy title");

        Map<String, String> itemSource = new HashMap<>();
        itemSource.put("title", "dummy title");
        model.itemSources.add(itemSource);

        model.atomToRSS();

        assertEquals(1, model.metaSource.size());
        assertEquals("dummy title", model.metaSource.get("title"));
        assertEquals(1, model.itemSources.size());
        assertEquals(1, model.itemSources.get(0).size());
        assertEquals("dummy title", model.itemSources.get(0).get("title"));
    }

    @Test
    @DisplayName("Test to turn Atom into RSS when there are both Atom and RSS fields")
    public void atomToRssAtomAndRssTest() {
        FeedModel model = new FeedModel();
        model.metaSource.put("title", "dummy title");
        model.metaSource.put("atom:title", "dummy atom title");

        Map<String, String> itemSource = new HashMap<>();
        itemSource.put("atom:rights", "dummy atom rights");
        itemSource.put("copyright", "dummy copyright");
        model.itemSources.add(itemSource);

        model.atomToRSS();

        assertEquals(1, model.metaSource.size());
        assertEquals("dummy title", model.metaSource.get("title"));
        assertEquals(1, model.itemSources.size());
        assertEquals(1, model.itemSources.get(0).size());
        assertEquals("dummy copyright", model.itemSources.get(0).get("copyright"));
    }
}
