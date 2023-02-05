package de.deroq.test;

import java.util.Collection;
import java.util.Set;

/**
 * @author Miles
 * @since 05.02.2023
 */
public interface Cache<K, V> {

    V put(K k, V v);

    boolean invalidate(K k);

    boolean invalidate(K k, V v);

    V get(K k);

    V replace(K k, V v);

    V replace(K k, V v, V v1);

    boolean containsKey(K k);

    boolean containsValue(V v);

    boolean clear();

    int size();

    boolean isEmpty();

    void putAll(Cache<K, V> cache);

    Set<Entry<K, V>> entrySet();

    Collection<K> keys();

    Collection<V> values();

    Set<K> keySet();

    Set<V> valueSet();

    interface Entry<K, V> {

        K getKey();

        V getValue();

        void setValue(V v);

        Entry<K, V> getNext();

        void setNext(Entry<K, V> next);

        int getHash();
    }
}
