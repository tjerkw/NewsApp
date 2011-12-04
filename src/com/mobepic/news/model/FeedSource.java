package com.mobepic.news.model;

public class FeedSource {
	public String uri;
	public String title;
	
	public FeedSource(String title, String uri) {
		this.title = title;
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}
	
	public String getTitle() {
		return title;
	}
}
