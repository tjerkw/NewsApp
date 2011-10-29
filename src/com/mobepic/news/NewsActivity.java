package com.mobepic.news;

import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.internal.view.menu.ActionMenuItem;
import com.actionbarsherlock.internal.view.menu.MenuItemImpl;
import com.actionbarsherlock.internal.view.menu.MenuItemWrapper;
import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem.OnMenuItemClickListener;

public class NewsActivity extends AbstractNewsActivity implements OnMenuItemClickListener {
	private FeedSource[] sources = {
		new FeedSource("Algemeen", "http://www.nu.nl/feeds/rss/algemeen.rss"),
		new FeedSource("Internet", "http://www.nu.nl/feeds/rss/internet.rss"),
		new FeedSource("Economie", "http://www.nu.nl/feeds/rss/economie.rss"),
		new FeedSource("Sport", "http://www.nu.nl/feeds/rss/sport.rss"),
		new FeedSource("Achterklap", "http://www.nu.nl/feeds/rss/achterklap.rss"),
		new FeedSource("Tech", "http://www.nu.nl/feeds/rss/tech.rss"),
		new FeedSource("Gadgets", "http://www.nu.nl/feeds/rss/gadgets.rss"),
		new FeedSource("NOS Headlines", "http://feeds.nos.nl/nosmyheadlines"),
		new FeedSource("NOS Binnenland", "http://feeds.nos.nl/nosnieuwsbinnenland"),
		new FeedSource("NOS Binnenland", "http://feeds.nos.nl/nosnieuwsbuitenland"),
		new FeedSource("Opmerkelijk", "http://www.nu.nl/feeds/rss/opmerkelijk.rss"),
		new FeedSource("Engadget", "http://www.engadget.com/rss.xml"),
		new FeedSource("Geenstijl", "http://www.geenstijl.nl/index.xml"),
		new FeedSource("Files", "http://www.vid.nl/VI/_rss")
	};
	protected List<FeedFragment> feedFragments = new ArrayList<FeedFragment>(3);
	
	protected void log(String msg) {
		Log.d("NewsApp", msg);
	}
	
	protected void onNewsService(NewsService service) {
		for(FeedFragment feedFragment : feedFragments ) {
			feedFragment.onConnected(service);
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_ACTION_BAR_ITEM_TEXT);
        setContentView(R.layout.main);
        
        ActionBar bar = this.getSupportActionBar();
        bar.setDisplayUseLogoEnabled(false);
        //bar.hide()
        
        ViewPager pager = (ViewPager)this.findViewById(R.id.pager);
        pager.setAdapter(new FeedsAdapter(this.getSupportFragmentManager()));
        
        //Bind the title indicator to the adapter
        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
        titleIndicator.setViewPager(pager);
	}
	
	interface NewsServiceListener {
		public void onConnected(NewsService service);
		public void onDisconnected(NewsService service);
	}
	
	
	public class FeedsAdapter extends FragmentPagerAdapter implements TitleProvider {
        public FeedsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return sources.length;
        }

        @Override
        public Fragment getItem(int position) {
        	
        	log("FeedAdapter.getItem "+position);
            if(position >= feedFragments.size()) {
            	FeedFragment fragment = FeedFragment.newInstance(sources[position]);
            	feedFragments.add(fragment);
            	if(getNewsService()!=null) {
            		fragment.onConnected(getNewsService());
            	}
            }
            return feedFragments.get(position);
        }

		@Override
		public String getTitle(int position) {
			return sources[position].getTitle();
		}
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {

    	
		MenuItem item = (MenuItem)menu.add("Refresh")
			.setOnMenuItemClickListener(this)
		    .setIcon(R.drawable.ic_refresh);
		
		if(item instanceof MenuItemWrapper) {
			((MenuItemWrapper)item).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else if(item instanceof MenuItemImpl) {
			((MenuItemImpl)item).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else if(item instanceof ActionMenuItem) {
			((ActionMenuItem)item).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}

		return true;
		//return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemClick(android.view.MenuItem item) {
		// refresh
		this.reload();
		return true;
	}

	private void reload() {
		for(FeedFragment fragment : feedFragments) {
			fragment.reload();
		}
	}
}