package com.student.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class TokenBlacklist {

    private static final int MAX_ENTRIES = 5000;
    private static final LinkedHashMap<String, Long> blacklist = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    public static synchronized void add(String token, long expirationTime) {
        blacklist.put(token, expirationTime);
    }

    public static synchronized boolean isBlacklisted(String token) {
        Long exp = blacklist.get(token);
        if (exp == null) {
            return false;
        }
        if (System.currentTimeMillis() > exp) {
            blacklist.remove(token);
            return false;
        }
        return true;
    }

    public static synchronized void cleanup() {
        long now = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> now > entry.getValue());
    }
}
