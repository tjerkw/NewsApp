package com.mobepic.wadup;

interface NewsServiceListener {
	public void onConnected(NewsService service);
	public void onDisconnected(NewsService service);
}
