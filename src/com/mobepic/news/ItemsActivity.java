package com.mobepic.news;

import java.util.ArrayList;
import java.util.List;

import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import com.mobepic.news.ItemsActivity.ItemsAdapter;
import com.mobepic.news.NewsActivity.FeedsAdapter;
import com.mobepic.news.NewsActivity.NewsServiceListener;
import com.mobepic.news.NewsService.FeedListener;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

public class ItemsActivity extends AbstractNewsActivity implements FeedListener {
	private String uri;
	private RSSFeed feed;
	private ViewPager pager;
	private ItemsAdapter adapter;
	private List<RSSItem> items;
	private int initialPosition;
	
	@Override
	protected void log(String msg) {
		Log.d("ItemsActivity", msg);
	}

	@Override
	protected void onNewsService(NewsService service) {
		service.getFeed(uri, this, false);
	}
	
	@Override
	public void onFeedRetrieved(RSSFeed feed) {
		log("onFeedRetrieved");
		this.items = feed.getItems();
		adapter.notifyDataSetChanged();
		pager.setCurrentItem(initialPosition);
	}

	@Override
	public void onFail(Exception e) {
		Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
	}

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = this.getIntent();
        Bundle params = intent.getExtras();
        if(params == null) {
        	throw new IllegalArgumentException("This activity requires params");
        }
		uri = params.getString("uri");
		initialPosition = params.getInt("position");
        
        //requestWindowFeature(Window.FEATURE_ACTION_BAR_ITEM_TEXT);
        setContentView(R.layout.items_activity);
        
        ActionBar bar = this.getSupportActionBar();
        bar.setDisplayUseLogoEnabled(false);
        bar.setDisplayShowHomeEnabled(true);
        //bar.hide()
        
        pager = (ViewPager)this.findViewById(R.id.pager);
        adapter = new ItemsAdapter(this.getSupportFragmentManager());
        pager.setAdapter(adapter);
        
        //Bind the title indicator to the adapter
        //TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
        //titleIndicator.setViewPager(pager);
	}
	
	public class ItemsAdapter extends FragmentPagerAdapter implements TitleProvider {
		private List<ItemFragment> itemsFragments = new ArrayList<ItemFragment>(10);
		
        public ItemsAdapter(FragmentManager fm) {
            super(fm);
        }
        
        @Override
        public int getCount() {
            return items==null ? 0 : items.size();
        }

        @Override
        public Fragment getItem(int position) {
        	
        	log("ItemsAdapter.getItem "+position);
            if(position >= itemsFragments.size()) {
            	ItemFragment fragment = ItemFragment.newInstance(uri, position);
            	itemsFragments.add(fragment);
            	fragment.onConnected(getNewsService());
            	return fragment;
            }
            return itemsFragments.get(position);
        }

		@Override
		public String getTitle(int position) {
			return items.get(position).getTitle();
		}
    }
}
