package org.demkiv.domain.util;

import lombok.ToString;

import java.util.concurrent.ConcurrentHashMap;

@ToString
public class CaptchaCache {

    private static CaptchaCache instance;
    private final ConcurrentHashMap<String, CacheEntry> cache;

    private CaptchaCache() {
        cache = new ConcurrentHashMap<>();
    }

    public static synchronized CaptchaCache getInstance() {
        if (instance == null) {
            instance = new CaptchaCache();
        }
        return instance;
    }

    public void put(String key, String value, long ttlMillis) {
        long expiryTime = System.currentTimeMillis() + ttlMillis;
        cache.put(key, new CacheEntry(value, expiryTime));
    }

    public String get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry != null && entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return (entry != null) ? entry.getValue() : null;
    }

    public void clearExpired() {
        for (String key : cache.keySet()) {
            CacheEntry entry = cache.get(key);
            if (entry != null && entry.isExpired()) {
                cache.remove(key);
            }
        }
    }
}
