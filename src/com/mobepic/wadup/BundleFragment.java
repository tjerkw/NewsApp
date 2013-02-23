package com.mobepic.wadup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mobepic.wadup.model.BundlesDatabase;
import com.mobepic.wadup.model.FeedBundle;
import com.viewpagerindicator.PageIndicator;

import java.util.HashMap;
import java.util.Map;

public class BundleFragment extends Fragment implements NewsServiceListener {
    private NewsService service;

    private static BundlesDatabase db = BundlesDatabase.getInstance();
    private FeedBundle bundle;
    protected Map<Integer, FeedFragment> feedFragments = new HashMap<Integer, FeedFragment>(10);

    private PageIndicator pageIndicator;
    private ViewPager pager;
    private ViewPager.OnPageChangeListener listener;
    private FeedsAdapter feedsAdapter;

    private void log(String msg) {
        Log.d("BundleFragment", msg);
    }

    public static BundleFragment newInstance(FragmentActivity context, int bundleIndex) {

        BundleFragment f = new BundleFragment();
        Bundle args = new Bundle();
        args.putInt("bundleIndex", bundleIndex);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        log("onCreateView");
        int bundleIndex = getArguments().getInt("bundleIndex");
        // return super.onCreateView(inflater, container, savedInstanceState);
        View contentView = inflater
                .inflate(R.layout.bundle_fragment, null, false);

        bundle = db.getBundles().get(bundleIndex);

        pageIndicator = (PageIndicator) contentView.findViewById(R.id.titles);
        pager = (ViewPager) contentView.findViewById(R.id.pager);

        // init adapter
        feedsAdapter = new FeedsAdapter(
            bundle,
            this.getChildFragmentManager()
        );
        pager.setAdapter(feedsAdapter);

        pageIndicator.setViewPager(pager);
        pageIndicator.setOnPageChangeListener(listener);

        return contentView;
    }

    @Override
    public void onDestroy() {
        if(pager!=null) {
            pager.setOnPageChangeListener(null);
        }

        listener = null;
        service = null;
        super.onDestroy();
    }

    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.listener = listener;
        if(pager!=null) {
            pager.setOnPageChangeListener(listener);
        }
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
}
