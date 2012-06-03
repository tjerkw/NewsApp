package com.mobepic.wadup;

import android.support.v4.util.LruCache;

public class Cache<K, V> {
	
	private LruCache<K, V> cache = new LruCache<K, V>(100);

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
		return cache.get(key);
	}

	public void put(K key, V value) {
		cache.put(key, value);
	}

	public void clear() {
		cache.evictAll();
	}
}
