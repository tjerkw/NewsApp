package com.mobepic.wadup;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mobepic.wadup.model.BundlesDatabase;
import com.mobepic.wadup.model.FeedBundle;

/**
 * @author tjerk
 * @date 2/23/13 12:22 AM
 */
public class MenuFragment extends ListFragment {
    private BundlesDatabase db = BundlesDatabase.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<FeedBundle> adapter = new ArrayAdapter<FeedBundle>(
            this.getActivity(),
            R.layout.menu_item,
            db.getBundles()
        );
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        //TODO: remove hard dependency with NewsActivity
        ((NewsActivity)this.getActivity()).showBundle(position);
    }

}
