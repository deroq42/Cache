/**
 * @author Miles
 * @since 07.02.2023
 */
public abstract class CacheLoader<K, V> {

    public abstract V load(K k);
}
