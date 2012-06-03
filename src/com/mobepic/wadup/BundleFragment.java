package com.mobepic.wadup;

import java.util.HashMap;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.mobepic.wadup.model.BundlesDatabase;
import com.mobepic.wadup.model.FeedBundle;
import com.viewpagerindicator.TitlePageIndicator;

public class BundleFragment implements NewsServiceListener {
	private NewsService service;
	
	private static BundlesDatabase db = BundlesDatabase.getInstance();
	private FeedBundle bundle;
	protected Map<Integer, FeedFragment> feedFragments = new HashMap<Integer, FeedFragment>(10);

	private TitlePageIndicator titleIndicator;
	private ViewPager pager;
	private FeedsAdapter feedsAdapter;
	
	private void log(String msg) {
		Log.d("BundleFragment", msg);
	}

	BundleFragment(FragmentActivity context, int bundleIndex) {
		bundle = db.getBundles().get(bundleIndex);

		titleIndicator = (TitlePageIndicator) context.findViewById(R.id.titles);
		pager = (ViewPager) context.findViewById(R.id.pager);

		// init adapter
		feedsAdapter = new FeedsAdapter(bundle, context.getSupportFragmentManager());
		pager.setAdapter(feedsAdapter);

		titleIndicator.setViewPager(pager);
	}

	@Override
	public void onConnected(NewsService service) {
		this.service = service;
		for(FeedFragment feedFragment : feedFragments.values() ) {
			feedFragment.onConnected(service);
		}
	}
	
	@Override
	public void onDisconnected(NewsService service) {
		for(FeedFragment feedFragment : feedFragments.values() ) {
			feedFragment.onDisconnected(service);
		}
		this.service = null;
	}

	public class FeedsAdapter extends FragmentPagerAdapter {
        private FeedBundle bundle;

		public FeedsAdapter(FeedBundle bundle, FragmentManager fm) {
            super(fm);
            this.bundle = bundle;
        }

        public void setBundle(FeedBundle bundle) {
			this.bundle = bundle;
			this.notifyDataSetChanged();
		}
        
        // this hack forces updates of the view
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

		@Override
        public int getCount() {
            return bundle == null ? 0 : bundle.getFeeds().size();
        }

        @Override
        public Fragment getItem(int position) {
        	
        	log("FeedAdapter.getItem "+position);
        	FeedFragment fragment = feedFragments.get(position);
            if(fragment==null) {
            	fragment = FeedFragment.newInstance(bundle.getFeeds().get(position));
            	feedFragments.put(position, fragment);
            	if(service!=null) {
            		fragment.onConnected(service);
            	}
            }
            return fragment;
        }

		@Override
		public CharSequence getPageTitle(int position) {
			return bundle.getFeeds().get(position).getTitle();
		}
    }
	
	public void setBundleIndex(int index) {
		feedFragments.clear();
		feedsAdapter.setBundle(db.getBundles().get(index));
		pager.setCurrentItem(0);
		pager.setAdapter(feedsAdapter);
	}
}
