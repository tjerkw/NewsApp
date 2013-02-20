package com.mobepic.wadup.model;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class FeedBundle {
    private String name;
    private List<FeedSource> sources = new ArrayList<FeedSource>();

    public FeedBundle(String name, FeedSource ... sources) {
        this.name = name;
        for(FeedSource source : sources) {
            Log.d("FeedBundle", "Adding source");
            this.sources.add(source);
        }
    }

    public String getName() {
        return name;
    }

    public List<FeedSource> getFeeds() {
        return sources;
    }

    public String toString() {
        return name;
    }
}
