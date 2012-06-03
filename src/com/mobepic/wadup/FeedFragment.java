package com.mobepic.wadup;

import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.mcsoxford.rss.RSSFeed;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

import com.mobepic.wadup.model.FeedSource;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import static com.mobepic.wadup.R.*;

/**
 * This fragment shows a list articles from a single feed.
 * Clicking on it brings you to the ItemFragment
 */
public class FeedFragment extends ListFragment implements NewsServiceListener,
        NewsService.FeedListener, PullToRefreshListView.OnRefreshListener {
    // the uri to the feed
    private String uri;
    private NewsService service;
    private boolean loaded;
    private PullToRefreshListView listView;
	private TextView errorText;

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
        // return super.onCreateView(inflater, container, savedInstanceState);
        View contentView = inflater
                .inflate(layout.feed_fragment, null, false);
        // Set a listener to be invoked when the list should be refreshed.
        listView = ((PullToRefreshListView) contentView
                .findViewById(id.list));
        listView.setOnRefreshListener(this);
	    errorText = (TextView) contentView.findViewById(R.id.error);

        if (service != null) {

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
        if (uri != null && !loaded) {
            log("onConnected->load");
            load(false);
        }
    }

    public void onStart() {
        super.onStart();
        if (service != null && uri != null && !loaded) {
            log("onStart->load");
            load(false);
        }
    }

    private synchronized void load(boolean reload) {

	    errorText.setVisibility(View.GONE);
        // get this feed!
        log("load: Getting feed from: " + uri);
        service.getFeed(uri, this, reload);
        loaded = true;
    }

    @Override
    public void onDisconnected(NewsService service) {
        log("onDisconnected from NewsService");
        Toast.makeText(this.getActivity(), "disconnected", Toast.LENGTH_SHORT)
                .show();
        this.service = null;
    }

    @Override
    public void onFeedRetrieved(final RSSFeed feed) {

        // woohhaa the world brings us news!
        log("Retrieved news: " + feed);
        final String lastUpdatedString = DateFormatUtils.ISO_TIME_NO_T_FORMAT
                .format(System.currentTimeMillis());
        this.getActivity().runOnUiThread(new Runnable() {
            public void run() {


	            errorText.setVisibility(View.GONE);
                setListAdapter(new FeedAdapter(getLayoutInflater(null), feed
                        .getItems()));

                listView.onRefreshComplete();
                listView.setReleaseLabel(lastUpdatedString);
            }
        });
    }

    @Override
    public void onFail(Exception e) {

        log("Feed retrieval failed " + e);
        Toast.makeText(this.getActivity(), "Failed loading feed: " + uri,
                Toast.LENGTH_LONG).show();
        listView.onRefreshComplete();
	    errorText.setText(e.getMessage());
	    errorText.setVisibility(View.VISIBLE);
    }

    public void onRefresh() {
        listView.setRefreshingLabel("Loading news", PullToRefreshBase.Mode.BOTH);
        this.load(true);
    }

}
