package de.deroq.test;

import java.util.concurrent.TimeUnit;

/**
 * @author Miles
 * @since 05.02.2023
 */
public final class CacheBuilder<K, V> {

    protected boolean expireAfterWrite = false;
    protected int expireAfterWriteTime;
    protected TimeUnit expireAfterWriteTimeUnit;

    private CacheBuilder() {}

    public static CacheBuilder<Object, Object> newBuilder() {
        return new CacheBuilder<>();
    }

    public CacheBuilder<Object, Object> expireAfterWrite(int time, TimeUnit timeUnit) {
        this.expireAfterWrite = true;
        this.expireAfterWriteTime = time;
        this.expireAfterWriteTimeUnit = timeUnit;
        return (CacheBuilder<Object, Object>) this;
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        return (Cache<K1, V1>) new LocalCache<>(this);
    }

    public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(CacheLoader<? super K1, V1> cacheLoader) {
        return (LoadingCache<K1, V1>) new LocalLoadingCache<>(this, cacheLoader);
    }
}
