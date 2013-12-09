package com.maxifier.mxcache.impl.caches.batch;

import gnu.trove.THashMap;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kochurov
 * Date: 25.04.12
 * Time: 15:57
 */
final class MapValueStrategy<K, V> implements ValueStrategy<K, Map<K, V>, V, Void> {
    private MapValueStrategy() {}

    private static final MapValueStrategy INSTANCE = new MapValueStrategy();

    public static <K, V> MapValueStrategy<K, V> getInstance() {
        //noinspection unchecked
        return INSTANCE;
    }

    @Override
    public Map<K, V> compose(Map<K, V> knownValue, Map<K, V> calculated, Void composition) {
        knownValue.putAll(calculated);
        return knownValue;
    }

    @Override
    public Map<K, V> createValue(Class<Map<K, V>> valueType, int n) {
        return new THashMap<K, V>(n);
    }

    @Override
    public Void createComposer(int n) {
        return null;
    }

    @Override
    public void addKnown(Map<K, V> knownValues, Void composer, int i, K k, V v) {
        knownValues.put(k, v);
    }

    @Override
    public void addUnknown(Map<K, V> knownValues, Void composer, int i, K k) {
    }

    @Override
    public V get(Map<K, V> value, int index, K key) {
        return value.get(key);
    }

    @Override
    public boolean requiresOrder() {
        return false;
    }
}