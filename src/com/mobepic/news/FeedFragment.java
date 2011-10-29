package com.mobepic.news;


import org.mcsoxford.rss.RSSFeed;

import com.mobepic.news.NewsActivity.NewsServiceListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class FeedFragment extends ListFragment implements NewsServiceListener, NewsService.FeedListener {
	// the uri to the feed
	private String uri;
	private NewsService service;
	private boolean loaded;
	
	private void log(String msg) {
		Log.d("FeedFragment", msg);
	}
	
    public static FeedFragment newInstance(FeedSource source) {

    	FeedFragment f = new FeedFragment();
    	Bundle args = new Bundle();
    	args.putString("uri", source.getUri());
        f.setArguments(args);
        return f;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
		log("onCreateView");
    	uri = getArguments().getString("uri");
    	if(service != null) {

			log("onCreateView->load");
    		load(false);
    	}
    	return super.onCreateView(inflater, container, savedInstanceState);
    	//View contentView = inflater.inflate(R.layout.feed_fragment, null);
		//return contentView;
	}
	
	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Intent intent = new Intent(getActivity(), ItemsActivity.class);
		intent.putExtra("uri", uri);
		intent.putExtra("position", position);
		this.startActivity(intent);
	}

	@Override
	public void onConnected(NewsService service) {
		this.service = service;
		if(uri!=null && !loaded) {
			log("onConnected->load");
			load(false);
		}
	}
	
	private synchronized void load(boolean reload) {

		// get this feed!
		log("load: Getting feed from: "+uri);
		service.getFeed(uri, this, reload);
		loaded = true;
	}

	@Override
	public void onDisconnected(NewsService service) {
		log("onDisconnected from NewsService");
		this.service = null;
	}

	@Override
	public void onFeedRetrieved(final RSSFeed feed) {
		
		// woohhaa the world brings us news!
		log("Retrieved news: "+feed);
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {

				setListAdapter(new FeedAdapter(getLayoutInflater(null), feed.getItems()));
			}
		});
	}

	@Override
	public void onFail(Exception e) {
		
		log("Feed failed " + e);
		Toast.makeText(this.getActivity(), "Failed loading feed: "+uri, Toast.LENGTH_LONG).show();
	}

	public void reload() {
		this.setListAdapter(null);
		this.setListShown(false); // show progress
		this.load(true);
	}

}
