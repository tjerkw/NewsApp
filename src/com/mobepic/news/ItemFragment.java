package com.mobepic.news;

import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import com.github.droidfu.widgets.WebImageView;
import com.mobepic.news.NewsActivity.NewsServiceListener;
import com.mobepic.news.NewsService.FeedListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ItemFragment extends Fragment implements NewsServiceListener, FeedListener {
	private NewsService service;
	// params
	private String uri;
	private int position;
	// data that we get from new service
	private RSSItem item;
	// ui
	private View contentView;
	private boolean requested;
	
	private static void log(String msg) {
		Log.d("ItemFragment", msg);
	}

	public static ItemFragment newInstance(String uri, int position) {
    	
		log("newInstance "+position+" "+uri);
		ItemFragment f = new ItemFragment();
    	Bundle args = new Bundle();
    	args.putString("uri", uri);
    	args.putInt("position", position);
        f.setArguments(args);
        return f;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
		log("onCreateView");
    	uri = getArguments().getString("uri");
    	position = getArguments().getInt("position");
    	contentView = inflater.inflate(R.layout.item_fragment, null);
    	
    	if(!requested && service!=null) {
			service.getFeed(uri, this, false);
    	}
    	
		return contentView;
	}

	@Override
	public void onConnected(NewsService service) {
		this.service = service;
		if(uri != null) {
			requested = true;
			service.getFeed(uri, this, false);
		}
	}

	@Override
	public void onDisconnected(NewsService service) {
		this.service = null;
	}

	@Override
	public void onFeedRetrieved(RSSFeed feed) {
		
		log("onFeedRetrieved");
		List<RSSItem> items = feed.getItems();
		if(position >= items.size()) {
			// unknown position;
			log("unkown position for item in feed");
			return;
		}
		item = items.get(position);
		WebImageView media = (WebImageView)contentView.findViewById(R.id.media);
		TextView title = (TextView)contentView.findViewById(R.id.title);
		TextView descr = (TextView)contentView.findViewById(R.id.title);
		TextView content = (TextView)contentView.findViewById(R.id.title);
		
		// fill it with data
		if(item.getThumbnails().size()>0) {
			media.setImageUrl(item.getThumbnails().get(0).getUrl().toString());
			media.loadImage();
		}
		title.setText(item.getTitle());
		descr.setText(item.getDescription());
		content.setText(item.getContent());
	}

	@Override
	public void onFail(Exception e) {
		// TODO Auto-generated method stub
		
	}
}
