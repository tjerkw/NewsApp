package com.mobepic.wadup;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.github.ignition.core.widgets.RemoteImageView;
import org.apache.commons.lang3.StringUtils;
import org.mcsoxford.rss.RSSItem;

import java.util.List;

public class FeedAdapter extends BaseAdapter {
    private List<RSSItem> items;
    private LayoutInflater inflater;
    private ItemActionListener listener;
    private final int MAX_DESCRIPTION_LENGTH = 250;

    private void log(String msg) {
        Log.d("FeedAdapter", msg);
    }

    FeedAdapter(LayoutInflater inflater, List<RSSItem> items, ItemActionListener listener) {
        this.listener = listener;
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

        wrapper.populateFrom((RSSItem)this.getItem(position), position);

        return(row);
    }


    class RSSItemWrapper {
        private TextView title=null;
        private TextView descr=null;
        private View row=null;
        private RemoteImageView media;
        private int position;
        private RSSItem item;

        RSSItemWrapper(View row) {
            this.row=row;
            View.OnClickListener onView = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(item, position);
                }
            };
            row.setOnClickListener(onView);
            Button viewButton = (Button)this.row.findViewById(R.id.view);
            viewButton.setOnClickListener(onView);
            Button shareButton = (Button)this.row.findViewById(R.id.share);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onShare(item);
                }
            });
            Button openInBrowserButton = (Button)this.row.findViewById(R.id.open_in_browser);
            openInBrowserButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onOpenInBrowser(item);
                }
            });
        }

        void populateFrom(RSSItem item, int position) {
            this.item = item;
            this.position = position;
            log("populateFrom " + item.getTitle());
            getTitle().setText(item.getTitle().trim());
            getDescription().setText(getDescription(item));
            if(item.getThumbnails()!=null && item.getThumbnails().size()>0) {
                log("thumbnails");
                RemoteImageView media = getMedia();
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

        private CharSequence getDescription(RSSItem item) {
            // force move from Spanned to normal String
            String descr = String.valueOf(item.getDescription());
            if(descr == null) {
                return "";
            }
            // remove weird [OBJ] characters: http://www.fileformat.info/info/unicode/char/fffc/index.htm
            descr = StringUtils.remove(descr, (char)65532);
            return StringUtils.abbreviate(descr.trim(), MAX_DESCRIPTION_LENGTH);
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

        RemoteImageView getMedia() {
            if (media==null) {
                media=(RemoteImageView)row.findViewById(R.id.media);
            }

            return media;
        }
    }


    interface ItemActionListener {
        void onItemClick(RSSItem item, int position);
        void onShare(RSSItem item);
        void onOpenInBrowser(RSSItem item);
    }
}
