package com.mobepic.news;


import org.apache.commons.lang3.time.DateFormatUtils;
import org.mcsoxford.rss.RSSFeed;

import com.markupartist.android.widget.PullToRefreshListView;

import com.mobepic.news.model.FeedSource;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class FeedFragment extends ListFragment implements NewsServiceListener, NewsService.FeedListener, PullToRefreshListView.OnRefreshListener {
	// the uri to the feed
	private String uri;
	private NewsService service;
	private boolean loaded;
	private PullToRefreshListView listView;
	
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
    	//return super.onCreateView(inflater, container, savedInstanceState);
    	View contentView = inflater.inflate(R.layout.feed_fragment, null, false);
    	// Set a listener to be invoked when the list should be refreshed.
    	listView = ((PullToRefreshListView) contentView.findViewById(android.R.id.list));
    	listView.setOnRefreshListener(this);
    	
    	if(service != null) {

			log("onCreateView->load");
    		load(false);
    	}
    	
		return contentView;
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
	
	public void onStart() {
		super.onStart();
		if(service!=null && uri!=null && !loaded) {
			log("onStart->load");
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
		Toast.makeText(this.getActivity(), "disconnected", Toast.LENGTH_SHORT).show();
		this.service = null;
	}

	@Override
	public void onFeedRetrieved(final RSSFeed feed) {
		
		// woohhaa the world brings us news!
		log("Retrieved news: "+feed);
		final String lastUpdatedString = DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(System.currentTimeMillis());
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {

				setListAdapter(new FeedAdapter(getLayoutInflater(null), feed.getItems()));
				
				listView.onRefreshComplete();
				listView.setLastUpdated(lastUpdatedString);
			}
		});
	}

	@Override
	public void onFail(Exception e) {
		
		log("Feed failed " + e);
		Toast.makeText(this.getActivity(), "Failed loading feed: "+uri, Toast.LENGTH_LONG).show();
		listView.onRefreshComplete();
	}

	public void onRefresh() {
		this.load(true);
	}

}
