package de.deroq.test;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Miles
 * @since 05.02.2023
 */
public class LocalCache<K, V> implements Cache<K, V> {

    protected final int MAX_CAPACITY = 10000;
    private final Node<K, V>[] table = new Node[MAX_CAPACITY];
    private final Set<Entry<K, V>> entrySet = new HashSet<>();

    private boolean expireAfterWrite = false;
    private int writeExpiryTime;
    private TimeUnit writeExpiryTimeUnit;
    private ScheduledExecutorService writeExpiryService;

    public LocalCache(CacheBuilder<K, V> builder) {
        if (builder.expireAfterWrite) {
            this.expireAfterWrite = true;
            this.writeExpiryTime = builder.expireAfterWriteTime;
            this.writeExpiryTimeUnit = builder.expireAfterWriteTimeUnit;
            this.writeExpiryService = Executors.newScheduledThreadPool(1);
        }
    }

    @Override
    public V put(K k, V v) {
        if (k == null) {
            return null;
        }

        final int hash = hash(k);
        final Node<K, V> node = new Node<>(k, v, null, hash);

        if (table[hash] == null) {
            table[hash] = node;
        } else {
            Node<K, V> previous = null;
            Node<K, V> current = table[hash];

            while (current != null) {
                if (current.getKey().equals(k)) {
                    node.setNext(current.getNext());

                    if (previous == null) {
                        table[hash] = node;
                    } else {
                        previous.setNext(node);
                    }
                }

                previous = current;
                current = (Node<K, V>) current.getNext();
            }

            if (previous != null) {
                previous.setNext(node);
            }
        }

        entrySet.add(node);

        if (expireAfterWrite) {
            writeExpiryService.schedule(() -> invalidate(k), writeExpiryTime, writeExpiryTimeUnit);
        }

        return v;
    }

    @Override
    public boolean invalidate(K k) {
        final int hash = hash(k);
        if (table[hash] == null) {
            return false;
        }

        final Node<K, V> node = table[hash];
        if (!node.getKey().equals(k)) {
            return false;
        }

        table[hash] = null;
        entrySet.remove(node);
        return true;
    }

    @Override
    public boolean invalidate(K k, V v) {
        final int hash = hash(k);
        if (table[hash] == null) {
            return false;
        }

        final Node<K, V> node = table[hash];
        if (!node.getKey().equals(k)) {
            return false;
        }

        if (!node.getValue().equals(v)) {
            return false;
        }

        table[hash] = null;
        entrySet.remove(node);
        return true;
    }

    @Override
    public V get(K k) {
        final int hash = hash(k);
        if (table[hash] == null) {
            return null;
        }

        final Node<K, V> node = table[hash];
        if (!node.getKey().equals(k)) {
            return null;
        }

        return node.getValue();
    }

    @Override
    public V replace(K k, V v) {
        final int hash = hash(k);
        if (table[hash] == null) {
            return null;
        }

        final Node<K, V> node = table[hash];
        if (!node.getKey().equals(k)) {
            return null;
        }

        entrySet.remove(node);
        node.setValue(v);
        entrySet.add(node);

        return v;
    }

    @Override
    public V replace(K k, V v, V v1) {
        final int hash = hash(k);
        if (table[hash] == null) {
            return null;
        }

        final Node<K, V> node = table[hash];
        if (!node.getKey().equals(k)) {
            return null;
        }

        if (!node.getValue().equals(v)) {
            return null;
        }

        entrySet.remove(node);
        node.setValue(v1);
        entrySet.add(node);

        return v1;
    }

    @Override
    public boolean containsKey(K k) {
        final int hash = hash(k);
        if (table[hash] == null) {
            return false;
        }

        Node<K, V> node = table[hash];
        if (!node.getKey().equals(k)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean containsValue(V v) {
        return values().contains(v);
    }

    @Override
    public boolean clear() {
        boolean b = false;
        for (K k : keys()) {
            b = invalidate(k);
        }

        return b;
    }

    @Override
    public int size() {
        return entrySet.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void putAll(Cache<K, V> cache) {
        for (Entry<K, V> entry : cache.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }

    @Override
    public Collection<K> keys() {
        Collection<K> collection = new ArrayList<>();
        for (Node<K, V> node : table) {
            collection.add(node.getKey());
        }

        return collection;
    }

    @Override
    public Collection<V> values() {
        Collection<V> collection = new ArrayList<>();
        for (Node<K, V> node : table) {
            collection.add(node.getValue());
        }

        return collection;
    }

    @Override
    public Set<K> keySet() {
        Set<K> set = new HashSet<>();
        for (Node<K, V> node : table) {
            set.add(node.getKey());
        }

        return set;
    }

    @Override
    public Set<V> valueSet() {
        Set<V> set = new HashSet<>();
        for (Node<K, V> node : table) {
            set.add(node.getValue());
        }

        return set;
    }

    protected int hash(Object key) {
        return Math.abs(key.hashCode()) % MAX_CAPACITY;
    }

    static class Node<K, V> implements Entry<K, V> {

        private final K key;
        private V value;
        private Entry<K, V> next;
        private final int hash;

        public Node(K key, V value, Entry<K, V> next, int hash) {
            this.key = key;
            this.value = value;
            this.next = next;
            this.hash = hash;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public Entry<K, V> getNext() {
            return next;
        }

        @Override
        public void setNext(Entry<K, V> next) {
            this.next = next;
        }

        @Override
        public int getHash() {
            return hash;
        }
    }
}
