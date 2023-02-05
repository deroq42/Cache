package de.deroq.test;

import java.util.concurrent.TimeUnit;

/**
 * @author Miles
 * @since 05.02.2023
 */
public final class CacheBuilder<K, V> {

    protected WriteExpiry writeExpiry;

    private CacheBuilder() {}

    public static CacheBuilder<Object, Object> newBuilder() {
        return new CacheBuilder<>();
    }

    public CacheBuilder<Object, Object> expireAfterWrite(int time, TimeUnit timeUnit) {
        this.writeExpiry = new WriteExpiry(time, timeUnit);
        return (CacheBuilder<Object, Object>) this;
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        return (Cache<K1, V1>) new LocaleCache<>(this);
    }

    protected class WriteExpiry {

        private final int time;
        private final TimeUnit timeUnit;

        public WriteExpiry(int time, TimeUnit timeUnit) {
            this.time = time;
            this.timeUnit = timeUnit;
        }

        public int getTime() {
            return time;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }
    }
}
