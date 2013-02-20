package com.mobepic.wadup.retriever;

import android.util.Log;
import com.mobepic.wadup.Article;
import de.jetwick.snacktory.Converter;
import de.jetwick.snacktory.HtmlFetcher;
import de.jetwick.snacktory.JResult;


/**
 * Retriever implementation using the snacktory lib
 *
 * @author tjerk
 * @created 5/31/12
 */
public class SnacktoryRetriever implements ArticleRetriever {
    private final static int RESOLVE_TIMEOUT = 5000;
    private final static int CONCURRENCY_LEVEL = 4;
    private static final String TAG = "SnacktoryRetriever";


    @Override
    public Article load(String url) throws ArticleRetrieveException {
        Log.d(TAG, "retrieving "+url);
        HtmlFetcher fetcher = new HtmlFetcher();
        // set cache. e.g. take the map implementation from google collections:
        //fetcher.setCache(new MapMaker().concurrencyLevel(20).expiration(10, TimeUnit.MINUTES).makeMap());
        de.jetwick.snacktory.Converter u = new Converter();

        final JResult res;
        try {
            boolean resolve = true;
            Log.d(TAG, "HtmlFetched fetcAndExtract " + url);
            res = fetcher.fetchAndExtract(url, RESOLVE_TIMEOUT, resolve);
        } catch (Throwable e) {
            Log.e(TAG, "Failed fetching "+url+", due to "+e);
            e.printStackTrace();
            throw new ArticleRetrieveException(e);
        }
        return new Article() {
            @Override
            public String getContent() {
                return res.getText();
            }

            @Override
            public String getTitle() {
                return res.getTitle();
            }

            @Override
            public String getImageUrl() {
                return res.getImageUrl();
            }
        };
    }
}
