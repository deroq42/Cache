package de.deroq.test;

/**
 * @author Miles
 * @since 07.02.2023
 */
public class LocalLoadingCache<K, V> extends LocalCache<K, V> implements LoadingCache<K, V> {

    private final CacheLoader<K, V> cacheLoader;

    public LocalLoadingCache(CacheBuilder<K, V> builder, CacheLoader cacheLoader) {
        super(builder);
        this.cacheLoader = cacheLoader;
    }

    @Override
    public V getUnchecked(K k) {
        V v;

        if (!containsKey(k)) {
            v = cacheLoader.load(k);
            put(k, v);
        } else {
            v = get(k);
        }

        return v;
    }
}
