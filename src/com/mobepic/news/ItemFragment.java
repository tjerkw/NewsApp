package com.mobepic.news;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import com.basistech.readability.Readability;
import com.github.droidfu.widgets.WebImageView;
import com.mobepic.news.NewsService.ArticleListener;
import com.mobepic.news.NewsService.FeedListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class ItemFragment extends Fragment implements NewsServiceListener, FeedListener, ArticleListener {
	private NewsService service;
	// params
	private String uri;
	private int position;
	// data that we get from new service
	private RSSItem item;
	// ui
	private View contentView;
	private WebImageView media;
	private TextView title;
	private TextView descr;
	private TextView content;
	private View spinner;
	private boolean requested;
	private Animation anim;
	
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
    	contentView = inflater.inflate(R.layout.item_fragment, null, false);

		media = (WebImageView)contentView.findViewById(R.id.media);
		title = (TextView)contentView.findViewById(R.id.title);
		descr = (TextView)contentView.findViewById(R.id.descr);
		content = (TextView)contentView.findViewById(R.id.content);
		spinner = contentView.findViewById(R.id.spinner);
		
		anim = AnimationUtils.loadAnimation(this.getActivity(),
				R.anim.drop_in_anim);
		content.setAnimation(anim);
		
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
			Toast.makeText(this.getActivity(), "unkown position for item in feed", Toast.LENGTH_LONG).show();
			return;
		}
		item = items.get(position);
		// do this in the ui thread
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				log("Showing item");
				
				// fill it with data
				if(item.getThumbnails().size()>0) {
					media.setVisibility(View.VISIBLE);
					media.setImageUrl(item.getThumbnails().get(0).getUrl().toString());
					media.loadImage();
				}
				title.setText(item.getTitle());
				
				descr.setText(getString(item.getDescription()));
				if(item.getContent()!=null) {
					content.setText(item.getContent());
					spinner.setVisibility(View.GONE);
				}
			}
		});
		if(item.getContent() == null) {
			service.getReadability(item.getLink().toString(), this);
		}
	}
	
	private String getString(CharSequence text) {
		// force move from Spanned to normal String
		String str = String.valueOf(text);
		if(str == null) {
			return "";
		}
		// remove weird [OBJ] characters: http://www.fileformat.info/info/unicode/char/fffc/index.htm
		str = StringUtils.remove(str, (char)65532);
		return str.trim();
	}

	@Override
	public void onFail(Exception e) {
		
		log("Feed failed " + e);
		Toast.makeText(this.getActivity(), "Failed loading feed: "+uri, Toast.LENGTH_LONG).show();
	}

	@Override
	public void onArticle(final Readability article) {
		// do this in the ui thread
		if(article.isImpossible()) {
			Toast.makeText(this.getActivity(), "Could not get full article", Toast.LENGTH_LONG).show();
		}
		this.getActivity().runOnUiThread(new Runnable() {
			public void run() {
				log("Showing full article");
				spinner.setVisibility(View.GONE);
				descr.setVisibility(View.GONE);
				content.setText(article.getArticleText());
				content.startAnimation(anim);
			}
		});
	}
}