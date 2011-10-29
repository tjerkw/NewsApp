package com.mobepic.news;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSLoader;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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

					feed = future.get();
					cache.put(uri, feed);
					listener.onFeedRetrieved(feed);
				} catch (InterruptedException e) {
					// we got interrupted.. just stop
				} catch (ExecutionException e) {
					listener.onFail(e);
				}
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
}
