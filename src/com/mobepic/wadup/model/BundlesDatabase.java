package com.mobepic.wadup.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Source of rss feeds, bundled in FeedBundle objects.
 * This class is a singleton to prevent unneeded memory usage
 *
 * @author tjerk
 */
public class BundlesDatabase {
    private List<FeedBundle> bundles = new ArrayList<FeedBundle>();
    private static BundlesDatabase instance;

    public static BundlesDatabase getInstance() {
        if(instance == null) {
            instance = new BundlesDatabase();
        }
        return instance;
    }

    private BundlesDatabase() {
        bundles.add(new FeedBundle("Nu.nl",
            new FeedSource("Algemeen", "http://www.nu.nl/feeds/rss/algemeen.rss"),
            new FeedSource("Internet", "http://www.nu.nl/feeds/rss/internet.rss"),
            new FeedSource("Economie", "http://www.nu.nl/feeds/rss/economie.rss"),
            new FeedSource("Sport", "http://www.nu.nl/feeds/rss/sport.rss"),
            new FeedSource("Achterklap", "http://www.nu.nl/feeds/rss/achterklap.rss"),
            new FeedSource("Tech", "http://www.nu.nl/feeds/rss/tech.rss"),
            new FeedSource("Gadgets", "http://www.nu.nl/feeds/rss/gadgets.rss"),
            new FeedSource("Opmerkelijk", "http://www.nu.nl/feeds/rss/opmerkelijk.rss")
        ));
        bundles.add(new FeedBundle("NOS Nieuws",
            new FeedSource("NOS Headlines", "http://feeds.nos.nl/nosmyheadlines"),
            new FeedSource("NOS Binnenland", "http://feeds.nos.nl/nosnieuwsbinnenland"),
            new FeedSource("NOS Binnenland", "http://feeds.nos.nl/nosnieuwsbuitenland")
        ));
        bundles.add(new FeedBundle("Tweakers",
            new FeedSource("Headlines", "http://tweakers.net/feeds/mixed.xml/direct"),
            new FeedSource("Core", "http://tweakers.net/feeds/core.xml/direct"),
            new FeedSource("Pro", "http://tweakers.net/feeds/pro.xml/direct"),
            new FeedSource("Games", "http://tweakers.net/feeds/games.xml/direct"),
            new FeedSource("Electronics", "http://tweakers.net/feeds/electronics.xml/direct"),
            new FeedSource("Mobile", "http://tweakers.net/feeds/mobile.xml/direct")
        ));
        /*
        bundles.add(new FeedBundle("Weblogs",
            new FeedSource("Geenstijl", "http://www.geenstijl.nl/index.xml"),
            //new FeedSource("Retecool", "http://retecool.com/feed/"),
            //new FeedSource("Planeet HS", "http://www.planeeths.nl/rss"),
            new FeedSource("VK Mag", "http://www.vkmag.com/magazine/rss_2.0/")
        ));
        */
        bundles.add(new FeedBundle("Tech",
            new FeedSource("Webwereld", "http://feeds.webwereld.nl/webwereld"),
            //new FeedSource("AndroidWorld", "http://feeds.feedburner.com/androidworld"), // not a valid rss stream
            new FeedSource("IPhoneclub", "http://feeds2.feedburner.com/iPhoneclub"),
            new FeedSource("Engadget", "http://hd.engadget.com/rss.xml")
        ));
    }

    public List<FeedBundle> getBundles() {
        return bundles;
    }
}
