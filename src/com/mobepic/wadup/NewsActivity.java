package com.mobepic.wadup;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.mobepic.wadup.model.BundlesDatabase;
import com.mobepic.wadup.model.FeedBundle;

public class NewsActivity extends AbstractNewsActivity implements ActionBar.OnNavigationListener {
	private BundlesDatabase db = BundlesDatabase.getInstance();
	private BundleFragment bundleView;
	
	protected void log(String msg) {
		Log.d("NewsApp", msg);
	}
	
	public void onConnected(NewsService service) {
		bundleView.onConnected(service);
	};
	
	public void onDisconnected(NewsService service) {
		if(bundleView!=null) {
			bundleView.onConnected(service);
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_ACTION_BAR_ITEM_TEXT);
        setContentView(R.layout.main);
        
        ActionBar bar = this.getSupportActionBar();
        bar.setDisplayUseLogoEnabled(false);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ArrayAdapter<FeedBundle> adapter = new ArrayAdapter<FeedBundle>(this, android.R.layout.simple_spinner_item, db.getBundles());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bar.setListNavigationCallbacks(adapter, this);
        
        bundleView = new BundleFragment(this, 0);
        //bar.hide()
	}

	@Override
	public boolean onNavigationItemSelected(int position, long itemId) {
		
		if(bundleView == null) {
			return true;
		}
		bundleView.setBundleIndex(position);
		return true;
	}
	

	/*
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	
    	
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
	*/
}