package com.mobepic.wadup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.github.ignition.core.widgets.RemoteImageView;
import com.mobepic.wadup.NewsService.ArticleListener;
import com.mobepic.wadup.NewsService.FeedListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.mcsoxford.rss.RSSFeed;
import org.mcsoxford.rss.RSSItem;

import java.util.List;

public class ItemFragment extends Fragment implements NewsServiceListener, FeedListener, ArticleListener {
    private NewsService service;
    // params
    private String uri;
    private int position;
    // data that we get from new service
    private RSSItem item;
    // ui
    private TextView errorText;

    private View contentView;
    private RemoteImageView media;
    private TextView title;
    private TextView date;
    private TextView descr;
    private TextView content;
    private Button openButton;
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

        errorText = (TextView)contentView.findViewById(R.id.error);

        media = (RemoteImageView)contentView.findViewById(R.id.media);
        title = (TextView)contentView.findViewById(R.id.title);
        date = (TextView)contentView.findViewById(R.id.date);
        descr = (TextView)contentView.findViewById(R.id.descr);
        content = (TextView)contentView.findViewById(R.id.content);
        spinner = contentView.findViewById(R.id.spinner);
        openButton = (Button)contentView.findViewById(R.id.open_button);
        openButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent();

                i.setAction(Intent.ACTION_VIEW);
                i.addCategory(Intent.CATEGORY_BROWSABLE);
                i.setData(item.getLink());
                startActivity(i);
            }

        });

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
                if(item.getPubDate() != null) {
                    date.setText(DateFormatUtils.ISO_DATETIME_FORMAT.format(item.getPubDate()));
                }

                descr.setText(getString(item.getDescription()));
                if(item.getContent()!=null) {
                    content.setText(item.getContent());
                    spinner.setVisibility(View.GONE);
                }
            }
        });
        if(item.getContent() == null) {
            String link = item.getLink().toString();
            log("Retrieving readability content from link " + link);
            service.getReadability(link, this);
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
    public void onFail(final Exception e) {

        log("Feed or article failed " + e);
        Toast.makeText(this.getActivity(), "Failed loading: "+uri, Toast.LENGTH_LONG).show();
        this.getActivity().runOnUiThread(new Runnable() {

        @Override
            public void run() {
                spinner.setVisibility(View.GONE);
                errorText.setText(e.getMessage());
                errorText.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onArticle(final Article article) {
        // do this in the ui thread

        this.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                log("Showing full article");
                spinner.setVisibility(View.GONE);
                //descr.setAnimation(null);
                //descr.setVisibility(View.GONE);
                content.setText(Html.fromHtml(article.getContent(), new HtmlmageGetter(), null));
                content.startAnimation(anim);
            }
        });
    }
}
