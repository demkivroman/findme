package org.demkiv.domain.util;

import lombok.Getter;

@Getter
public class CacheEntry {

    final String value;
    final long expiryTime;

    CacheEntry(String value, long expiryTime) {
        this.value = value;
        this.expiryTime = expiryTime;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}
