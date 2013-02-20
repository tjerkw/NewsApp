package com.mobepic.wadup;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import com.mobepic.wadup.retriever.ArticleRetrieveException;
import com.mobepic.wadup.retriever.ArticleRetriever;
import com.mobepic.wadup.retriever.ArticleRetrieverFactory;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSLoader;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewsService extends Service {
    private RSSLoader loader;
    private ExecutorService executor;
    private Cache<String, RSSFeed> cache;

    public class LocalBinder extends Binder {
        NewsService getService() {
            return NewsService.this;
        }
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void onCreate() {

        log("onCreate");
        super.onCreate();
        cache = new Cache<String, RSSFeed>();
        loader = RSSLoader.fifo();
        executor = Executors.newFixedThreadPool(4);
    }

    public void getFeed(final String uri, final FeedListener listener, boolean renew) {
        RSSFeed feed = cache.get(uri);
        if(!renew && feed!=null) {
            log("Feed found in cache "+uri);
            listener.onFeedRetrieved(feed);
            return;
        }
        log("Submitting getFeed to executor "+uri);
        executor.submit(new Runnable() {
            public void run() {
                Future<RSSFeed> future = loader.load(uri);
                // block this thread, wait for the rsult
                RSSFeed feed;
                try {
                    log("future waiting for feed return");
                    feed = future.get();
                    cache.put(uri, feed);
                    log("Feed retrieved");
                    listener.onFeedRetrieved(feed);
                } catch (InterruptedException e) {
                    // we got interrupted.. just stop
                    Log.e("NewsService", "Got interrupted during retrieval of feed" + e);
                } catch (ExecutionException e) {
                    listener.onFail(e);
                }
            }
        });
    }

    public void getReadability(final String uri, final ArticleListener listener) {

        log("Submitting getReadability to executor "+uri);
        executor.submit(new Runnable() {
            public void run() {

                log("Instantiation article factory");
                ArticleRetriever retriever = ArticleRetrieverFactory.getInstance();
                Article article = null;
                try {
                    log("Load article");
                    article = retriever.load(uri);
                    listener.onArticle(article);

                } catch (ArticleRetrieveException e) {
                    e.printStackTrace();
                    e.printStackTrace();
                    if(e.getCause()!=null) {
                        e.getCause().printStackTrace();
                    }
                    listener.onFail(e);
                }
                /*
                Readability r = new Readability();

                // this call is quite heavy on resources
                try {
                    r.processDocument(uri);
                    listener.onArticle(r);

                } catch (PageReadException e) {

                    e.printStackTrace();
                    if(e.getCause()!=null) {
                        e.getCause().printStackTrace();
                    }
                    listener.onFail(e);
                }
                */

            }
        });
    }

    private void log(String msg) {
        Log.d("NewsSerice", msg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
        cache.clear();
        cache = null;
        loader = null;
    }

    public interface FeedListener {
        public void onFeedRetrieved(RSSFeed feed);
        public void onFail(Exception e);
    }

    public interface ArticleListener {
        public void onArticle(Article article);
        public void onFail(Exception e);
    }
}
