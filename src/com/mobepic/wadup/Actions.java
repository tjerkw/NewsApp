package com.mobepic.wadup;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import org.mcsoxford.rss.RSSItem;

/**
 * @author tjerk
 * @date 6/24/12 1:15 AM
 */
public class Actions {

    static void share(Context ctx, RSSItem item) {
        // share article
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, item.getTitle());
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, item.getLink() + "\n\n" + item.getDescription());

        try {

            ctx.startActivity(Intent.createChooser(shareIntent, ctx.getText(R.string.share_chooser_title)));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ctx, R.string.share_not_possible, Toast.LENGTH_SHORT).show();
        }
    }

    public static void openInBrowser(Context ctx, RSSItem item) {
        Intent i = new Intent();

        i.setAction(Intent.ACTION_VIEW);
        i.addCategory(Intent.CATEGORY_BROWSABLE);
        i.setData(item.getLink());
        ctx.startActivity(i);
    }
}
