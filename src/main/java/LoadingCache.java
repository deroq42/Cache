/**
 * @author Miles
 * @since 07.02.2023
 */
public interface LoadingCache<K, V> extends Cache<K, V> {

    V getUnchecked(K k);
}
