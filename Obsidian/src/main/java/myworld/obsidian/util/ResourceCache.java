package myworld.obsidian.util;

import java.util.*;
import java.util.function.BiConsumer;

public class ResourceCache<K, V> {

    protected final Map<K, V> cache;

    public ResourceCache(){
        cache = new IdentityHashMap<>();
    }

    public synchronized V cache(K key, V value){
        return cache.putIfAbsent(key, value);
    }

    public synchronized V get(K key){
        return cache.get(key);
    }

    public synchronized V remove(K key){
        return cache.remove(key);
    }

    public synchronized Collection<V> values(){
        return cache.values();
    }

    public synchronized void clear(){
        clear((k, v) -> {});
    }

    public synchronized void clear(BiConsumer<K, V> cleaner){
        cache.forEach(cleaner);
        cache.clear();
    }

}
