package com.reader.entity.util;

/**
 * @author      ：李冠良
 * @description ：表示一个键值对
 * @date        ：2025 2月 17 18:22
 */


public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() { return key; }
    public V getValue() { return value; }

    public void setKey(K key) { this.key = key; }
    public void setValue(V value) { this.value = value; }
}