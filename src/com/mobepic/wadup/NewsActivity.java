package com.mobepic.wadup;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.ActionBar;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.view.MenuItem;
import com.mobepic.wadup.model.BundlesDatabase;
import com.mobepic.wadup.model.FeedBundle;
import com.slidingmenu.lib.SlidingMenu;

public class NewsActivity extends AbstractNewsActivity implements ActionBar.OnNavigationListener {
    private SlidingMenu menu;
    private BundlesDatabase db = BundlesDatabase.getInstance();
    private BundleFragment bundleFragment;
    private NewsService service;

    protected void log(String msg) {
        Log.d("NewsApp", msg);
    }

    public void onConnected(NewsService service) {
        this.service = service;
        bundleFragment.onConnected(service);
    };

    public void onDisconnected(NewsService service) {
        this.service = null;
        if(bundleFragment !=null) {
            bundleFragment.onDisconnected(service);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_ACTION_BAR_ITEM_TEXT);
        setContentView(R.layout.main);

        ActionBar bar = this.getSupportActionBar();
        bar.setDisplayUseLogoEnabled(false);
        //bar.setListNavigationCallbacks(adapter, this);

        bar.setDisplayHomeAsUpEnabled(true);

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        //menu.setFadeDegree(0.35f);
        menu.setFadeEnabled(false);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.menu);
        showBundle(0);
        //bar.hide()
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menu.toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long itemId) {

        if(bundleFragment == null) {
            return true;
        }
        showBundle(position);
        return true;
    }

    void showBundle(int bundleId) {

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        if(bundleFragment!=null) {
            t.remove(bundleFragment);
        }
        bundleFragment = BundleFragment.newInstance(this, bundleId);
        bundleFragment.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int position) {
                log("onPageSelected: " + position);
                switch (position) {
                    case 0:
                        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                        break;
                    default:
                        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                        break;
                }
            }

        });

        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.toggle();

        if(this.service!=null) {
            bundleFragment.onConnected(service);
        }
        t.add(R.id.bundle_fragment_container, bundleFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.showContent();
        } else {
            super.onBackPressed();
        }
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