package com.mobepic.news;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public abstract class AbstractNewsActivity extends FragmentActivity {
	private NewsService service;
	
	protected abstract void log(String msg);
	
	private ServiceConnection svcConn=new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder binder) {
			service=((NewsService.LocalBinder)binder).getService();
			onNewsService(service);
		}

		public void onServiceDisconnected(ComponentName className) {
			service=null;
		}
	};
	
	protected abstract void onNewsService(NewsService service);
	
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