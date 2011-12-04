package com.mobepic.news;

interface NewsServiceListener {
	public void onConnected(NewsService service);
	public void onDisconnected(NewsService service);
}
