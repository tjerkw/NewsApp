package com.mobepic.news;

import java.util.List;

import org.mcsoxford.rss.RSSItem;

import com.github.droidfu.widgets.WebImageView;
import com.viewpagerindicator.TitleProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FeedAdapter extends BaseAdapter implements TitleProvider {
	private List<RSSItem> items;
	private LayoutInflater inflater;
	
	private void log(String msg) {
		Log.d("FeedAdapter", msg);
	}
	
	FeedAdapter(LayoutInflater inflater, List<RSSItem> items) {
		this.inflater = inflater;
		this.items = items;
		log("FeedAdapter with n items: "+items.size());
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public String getTitle(int position) {
		return items.get(position).getTitle();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row=convertView;
		RSSItemWrapper wrapper=null;

		if (row==null) {													
			row=inflater.inflate(R.layout.feed_item_row, parent, false);
			wrapper=new RSSItemWrapper(row);
			row.setTag(wrapper);
			log("Creating row at position "+position);
		}
		else {
			wrapper=(RSSItemWrapper)row.getTag();
		}

		wrapper.populateFrom((RSSItem)this.getItem(position));

		return(row);
	}


	class RSSItemWrapper {
		private TextView title=null;
		private TextView descr=null;
		private View row=null;
		private WebImageView media;

		RSSItemWrapper(View row) {
			this.row=row;
		}

		void populateFrom(RSSItem item) {
			log("populateFrom " + item.getTitle());
			getTitle().setText(item.getTitle());
			getDescription().setText(item.getDescription().toString());
			if(item.getThumbnails()!=null && item.getThumbnails().size()>0) {
				log("thumbnails");
				WebImageView media = getMedia();
				// calling reset is important in order to make sure an old image from the recycled
				// view is not displayed while loading
				media.reset();
				media.setVisibility(View.VISIBLE);
				media.setImageUrl(item.getThumbnails().get(0).getUrl().toString());
				media.loadImage();
			} else {
				log("No thumbss");
			}
		}

		TextView getTitle() {
			if (title==null) {
				title=(TextView)row.findViewById(R.id.title);
			}
			return title;
		}

		TextView getDescription() {
			if (descr==null) {
				descr=(TextView)row.findViewById(R.id.descr);
			}

			return(descr);
		}
		
		WebImageView getMedia() {
			if (media==null) {
				media=(WebImageView)row.findViewById(R.id.media);
			}

			return media;
		}
	}

}
