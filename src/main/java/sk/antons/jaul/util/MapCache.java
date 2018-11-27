/*
 * Copyright 2015 Anton Straka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.antons.jaul.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Simple Map cache. It imple,ments fully Map integrace so it can me synchronized. 
 * It is possible to set size limit and expiration in milliseconds. 
 * 
 * It is recommended to use only get and put methods to achieve expected results. 
 * 
 * @author antons
 */
public class MapCache<K, V> implements Map<K, V> {
   
    private int limit = -1;
    private long expiration = -1;
    private Map<K, Entry<K, V>> cache = null;
    private Entry<K, V> head = null;
    private Entry<K, V> tail = null;
    
    public MapCache() {
        cache = new HashMap<K, Entry<K, V>>();
    }
    
    public MapCache(int initSize) {
        cache = new HashMap<K, Entry<K, V>>(initSize);
    }

    public static <KK, VV> MapCache<KK, VV> instance(Class<KK> keyType, Class<VV> valueType) {
        return new MapCache<KK, VV>();
    }
    
    public static <KK, VV> MapCache<KK, VV> instance(Class<KK> keyType, Class<VV> valueType, int initSize) {
        return new MapCache<KK, VV>(initSize);
    }
    
    public int limit() { return limit; }
    public MapCache<K, V> limit(int limit) { this.limit = limit; return this; }
    
    public long expiration() { return expiration; }
    public MapCache<K, V> expiration(long expiration) { this.expiration = expiration; return this; }
    
    public Map<K, V> synchronize() { return Collections.synchronizedMap(this); }
    
    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    private Entry<K, V> expire(Entry<K, V> entry) {
        if(entry == null) return null;
        if(expiration < 1) return entry;
        long time = System.currentTimeMillis();
        if(entry.time >= time) return entry; 
        tail = entry.prev;
        if(tail == null) head = null;
        else tail.next = null;
        while(entry != null) {
            cache.remove(entry.key);
            entry = entry.next;
        }
        return null;
    }
    
    private void remove(Entry<K, V> entry) {
        if(entry == null) return ;
        cache.remove(entry.key);
        if(entry.prev == null) {
            head = entry.next;
            if(head != null) head.prev = null;
        } else {
            entry.prev.next = entry.next;
        }
        if(entry.next == null) {
            tail = entry.prev;
            if(tail != null) tail.next = null;
        } else {
            entry.next.prev = entry.prev;
        }
    }

    public boolean containsKey(Object key) {
        if(key == null) return false;
        long time = System.currentTimeMillis();
        Entry<K, V> entry = cache.get(key);
        entry = expire(entry);
        return entry != null;
    }

    public boolean containsValue(Object value) {
        long time = System.currentTimeMillis();
        for(Map.Entry<K, Entry<K, V>> entry : cache.entrySet()) {
            Entry<K, V> en = entry.getValue();
            if(en.time < time) continue;
            if((value == null) && (en.value == null)) return true;
            if(value.equals(en.value)) return true;
        }
        return false;
    }

    public V get(Object key) {
        Entry<K, V> entry = cache.get(key);
        entry = expire(entry);
        if(entry == null) return null;
        return entry.value;
    }

    public V put(K key, V value) {
        if(key == null) throw new IllegalArgumentException("Null key is not allowed");
        Entry<K, V> entry = cache.get(key);
        if(entry != null) remove(entry);
        if((limit > 0) && size() >= limit) remove(tail);
        entry = new Entry();
        entry.key = key;
        entry.value = value;
        if(tail == null) {
            tail = entry;
            head = entry;
        } else {
            head.prev = entry;
            entry.next = head;
            head = entry;
        }
        cache.put(key, entry);
        return value;
    }

    public V remove(Object key) {
        if(key == null) return null;
        Entry<K, V> entry = cache.get(key);
        if(entry == null) return null;
        remove(entry);
        return entry.value;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        if(m == null) return ;
        for(Map.Entry<?, ?> object : m.entrySet()) {
            put((K)object.getKey(), (V)object.getValue());
        }
    }

    public void clear() {
        cache.clear();
        head = null;
        tail = null;
    }

    public Set<K> keySet() {
        return cache.keySet();
    }

    public Collection<V> values() {
        List<V> list = new ArrayList<V>();
        for(Map.Entry<?, ?> object : cache.entrySet()) {
            list.add((V)object.getValue());
        }
        return list;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    private class Entry<K, V> {
        public K key;
        public V value;
        public long time = MapCache.this.expiration < 1 ? Long.MAX_VALUE : System.currentTimeMillis() + MapCache.this.expiration;
        public Entry<K, V> prev;
        public Entry<K, V> next;
    }

    public String dunp() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nkeys: ").append(cache.keySet());
        sb.append("\nhead: ").append((head == null)?"":head.key);
        sb.append("\ntail: ").append((tail == null)?"":tail.key);
        sb.append("\ndesc: ");
        Entry<K, V> entry = head;
        while(entry != null) {
            sb.append(", ").append(entry.key);
            entry = entry.next;
        }
        sb.append("\nasc: ");
        entry = tail;
        while(entry != null) {
            sb.append(", ").append(entry.key);
            entry = entry.prev;
        }
        return sb.toString();
    }

    
}
