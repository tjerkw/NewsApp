package com.mobepic.wadup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.view.MenuItemCompat;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import com.mobepic.wadup.NewsService.FeedListener;
import com.viewpagerindicator.CirclePageIndicator;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;

public class ItemsActivity extends AbstractNewsActivity
	implements FeedListener, com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener
{
	private String uri;
	private RSSFeed feed;
	private ViewPager pager;
	private ItemsAdapter adapter;
	private List<RSSItem> items;
	private int initialPosition;
	private CirclePageIndicator indicator;
	protected Map<Integer, ItemFragment> itemFragments = new HashMap<Integer, ItemFragment>(10);

	@Override
	protected void log(String msg) {
		Log.d("ItemsActivity", msg);
	}

	@Override
	public void onConnected(NewsService service) {
		service.getFeed(uri, this, false);
		// update if needed
		for(ItemFragment fragment : itemFragments.values() ) {
			fragment.onConnected(service);
		}
	}

	@Override
	public void onDisconnected(NewsService service) {
		service.getFeed(uri, this, false);
		// update if needed
		for(ItemFragment fragment : itemFragments.values() ) {
			fragment.onDisconnected(service);
		}
	}

	@Override
	public void onFeedRetrieved(RSSFeed feed) {
		log("onFeedRetrieved");
		this.items = feed.getItems();
		adapter.notifyDataSetChanged();
		pager.setCurrentItem(initialPosition-1); // its 1 off!
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
		bar.setDisplayHomeAsUpEnabled(true);
		//bar.hide()

		pager = (ViewPager)this.findViewById(R.id.pager);
		adapter = new ItemsAdapter(this.getSupportFragmentManager());
		pager.setAdapter(adapter);

		//Bind the indicator to the adapter
		indicator = (CirclePageIndicator)findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	public class ItemsAdapter extends FragmentPagerAdapter {

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
			ItemFragment fragment = itemFragments.get(position);
			if(fragment==null) {
				fragment = ItemFragment.newInstance(uri, position);
				itemFragments.put(position, fragment);
				if(getNewsService()!=null) {
					fragment.onConnected(getNewsService());
				}
			}
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return items.get(position).getTitle();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		/*
		((MenuItemCompat)menu.add(R.string.share)
			.setIcon(R.drawable.ic_menu_share)
			.setOnMenuItemClickListener(this);
		*/

		menu.add(R.string.open_in_browser)
				.setOnMenuItemClickListener(this);

		return true;
		//return super.onCreateOptionsMenu(menu);
	}

	private RSSItem getCurrentItem() {
		if(pager == null || items == null || indicator==null || pager.getCurrentItem() >= items.size()) {
			return null;
		}
		return items.get(pager.getCurrentItem());
	}

	public boolean onMenuItemClick(android.view.MenuItem item) {
		return onItemClick(item.getItemId());
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		return onItemClick(item.getItemId());
	}

	private boolean onItemClick(int itemId) {

		RSSItem item = getCurrentItem();
		if(item == null) {
			return false; // no item visible
		}

		if(itemId == 0) {

			Actions.share(this, item);
		} else {
			Actions.openInBrowser(this, item);
		}
		return true;
	}
}
