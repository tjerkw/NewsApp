package com.mobepic.wadup;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.drawable.Drawable;
import android.text.Html;

public class HtmlmageGetter implements Html.ImageGetter {

	@Override
	public Drawable getDrawable(String url) {
		try {
			// cache dir + name of image + the image save format
			return downloadImage(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Drawable downloadImage(String url) throws IOException {
		URL myFileUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
		conn.setDoInput(true);
		conn.connect();
		InputStream is = conn.getInputStream();

		return Drawable.createFromStream(is, "src");
	}
}