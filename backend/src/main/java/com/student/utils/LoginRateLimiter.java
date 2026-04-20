package com.student.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LoginRateLimiter {

    private static final int MAX_FAILURES = 5;
    private static final long LOCK_DURATION_MS = 15 * 60 * 1000L;
    private static final long WINDOW_DURATION_MS = 5 * 60 * 1000L;
    private static final int MAX_ENTRIES = 1000;

    private static final LinkedHashMap<String, LoginAttempt> attempts = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, LoginAttempt> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    public static synchronized void recordFailure(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null || attempt.isExpired()) {
            attempt = new LoginAttempt();
        }
        attempt.increment();
        attempts.put(key, attempt);
    }

    public static synchronized void recordSuccess(String key) {
        attempts.remove(key);
    }

    public static synchronized boolean isLocked(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null) {
            return false;
        }
        if (attempt.isLocked()) {
            return true;
        }
        if (attempt.isExpired()) {
            attempts.remove(key);
            return false;
        }
        return false;
    }

    public static synchronized long getRemainingLockTime(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null || !attempt.isLocked()) {
            return 0;
        }
        return attempt.getRemainingLockTime();
    }

    public static String buildKey(String ip, String username) {
        return ip + ":" + username;
    }

    private static class LoginAttempt {
        private int failureCount;
        private long firstFailureTime;
        private long lockTime;

        LoginAttempt() {
            this.failureCount = 0;
            this.firstFailureTime = System.currentTimeMillis();
            this.lockTime = 0;
        }

        void increment() {
            if (isExpired()) {
                this.failureCount = 0;
                this.firstFailureTime = System.currentTimeMillis();
                this.lockTime = 0;
            }
            this.failureCount++;
            if (this.failureCount >= MAX_FAILURES && this.lockTime == 0) {
                this.lockTime = System.currentTimeMillis();
            }
        }

        boolean isLocked() {
            if (lockTime == 0) return false;
            return System.currentTimeMillis() - lockTime < LOCK_DURATION_MS;
        }

        boolean isExpired() {
            if (lockTime > 0 && isLocked()) return false;
            return System.currentTimeMillis() - firstFailureTime > WINDOW_DURATION_MS;
        }

        long getRemainingLockTime() {
            if (lockTime == 0) return 0;
            long remaining = LOCK_DURATION_MS - (System.currentTimeMillis() - lockTime);
            return remaining > 0 ? remaining / 60000 + 1 : 0;
        }
    }
}
