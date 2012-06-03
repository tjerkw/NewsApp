package com.mobepic.wadup;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;

import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Provides a subclass for any activity that interacts with the NewsService.
 * 
 * @author tjerk
 */
public abstract class AbstractNewsActivity extends SherlockFragmentActivity implements NewsServiceListener {
	private NewsService service;
	
	protected abstract void log(String msg);
	
	private ServiceConnection svcConn=new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			service=((NewsService.LocalBinder)binder).getService();
			onConnected(service);
		}

		public void onServiceDisconnected(ComponentName className) {
			onDisconnected(service);
			service=null;
		}
	};
	
	protected NewsService getNewsService() {
		return service;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("bindService");
		bindService(new Intent(this, NewsService.class), svcConn, BIND_AUTO_CREATE);
	}
	
	public void onDestroy() {
		super.onDestroy();
		unbindService(svcConn);
	}
}