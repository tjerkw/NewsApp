package com.mobepic.news;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.mcsoxford.rss.RSSFeed;

public class Cache<K, V> {
	// softreference creates a cache that will never use too much memory
	private Map<K, SoftReference<V>> cache = new HashMap<K, SoftReference<V>>();

	/**
	 * Gets a value for a specific key from the cache,
	 * or null if it was never in the cache, or removed at any time.
	 * Note that once you reference a value from the cache it will 
	 * never be removed.
	 * 
	 * @param key
	 * @return value
	 */
	public V get(K key) {
		if(cache.get(key)!=null) {
			// hard reference it, prevents garbage collection
			V value = cache.get(key).get();
			return value; // maybe null if softreference was garbage collected
		} else {
			return null;
		}
	}

	public void put(K key, V value) {
		cache.put(key, new SoftReference<V>(value));
	}

	public void clear() {
		cache.clear();
	}
}
