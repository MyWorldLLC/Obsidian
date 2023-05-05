/*
 *    Copyright 2022 MyWorld, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package myworld.obsidian.properties;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MapProperty<K, V> extends Property<MapChangeListener<K, V>> implements Map<K, V> {

    protected final Map<K, V> values;

    public MapProperty(){
        values = new ConcurrentHashMap<>();
        bindListener = new MapChangeListener<>() {
            @Override
            public void onAdd(MapProperty<K, V> prop, K key, V value){
                values.put(key, value);
            }

            @Override
            public void onChange(MapProperty<K, V> prop, K key, V oldValue, V newValue){
                values.put(key, newValue);
            }

            @Override
            public void onRemove(MapProperty<K, V> prop, K key, V value){
                values.remove(key);
            }

        };
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return values.get(key);
    }

    @Override
    public V put(K key, V value) {
        var oldValue = values.put(key, value);
        if(oldValue == null){
            listeners.forEach(l -> l.onAdd(this, key, value));
        }else{
            listeners.forEach(l -> l.onChange(this, key, oldValue, value));
        }
        return oldValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        var oldValue = values.remove(key);
        if(oldValue != null){
            listeners.forEach(l -> l.onRemove(this, (K)key, oldValue));
        }
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        values.forEach((k, v) -> listeners.forEach(l -> l.onRemove(this, k, v)));
        values.clear();
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(values.keySet());
    }

    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(values.values());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(values.entrySet());
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        return values.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
        values.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        values.entrySet().forEach(entry -> {
            var oldValue = entry.getValue();
            var newValue = function.apply(entry.getKey(), entry.getValue());
            listeners.forEach(l -> l.onChange(this, entry.getKey(), oldValue, newValue));
            entry.setValue(newValue);
        });
    }

    @Override
    public V putIfAbsent(K key, V value) {
        var mappedValue = values.putIfAbsent(key, value);
        if(mappedValue == null){
            listeners.forEach(l -> l.onAdd(this, key, value));
        }
        return mappedValue;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object key, Object value) {
        var removed = values.remove(key, value);
        if(removed){
            listeners.forEach(l -> l.onRemove(this, (K)key, (V)value));
        }
        return removed;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        var replaced = values.replace(key, oldValue, newValue);
        if(replaced){
            listeners.forEach(l -> l.onChange(this, key, oldValue, newValue));
        }
        return replaced;
    }

    @Override
    public V replace(K key, V value) {
        var oldValue = values.replace(key, value);
        if(oldValue != null){
            listeners.forEach(l -> l.onChange(this, key, oldValue, value));
        }
        return oldValue;
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        var contains = values.containsKey(key);
        var value = values.computeIfAbsent(key, mappingFunction);
        if(!contains){
            listeners.forEach(l -> l.onAdd(this, key, value));
        }
        return value;
    }

    @Override
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        var oldValue = values.get(key);
        var newValue = values.computeIfPresent(key, remappingFunction);
        if(newValue == null){
            listeners.forEach(l -> l.onRemove(this, key, oldValue));
        }else{
            listeners.forEach(l -> l.onChange(this, key, oldValue, newValue));
        }
        return newValue;
    }

    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        var oldValue = values.get(key);
        var newValue = values.compute(key, remappingFunction);

        if(oldValue == null && newValue != null){
            listeners.forEach(l -> l.onAdd(this, key, newValue));
        }else if(oldValue != null && newValue != null){
            listeners.forEach(l -> l.onChange(this, key, oldValue, newValue));
        }else if(oldValue != null && newValue == null){
            listeners.forEach(l -> l.onRemove(this, key, oldValue));
        }

        return newValue;
    }

    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        var oldValue = values.get(key);
        var mergedValue = values.merge(key, value, remappingFunction);

        if(oldValue == null && mergedValue != null){
            listeners.forEach(l -> l.onAdd(this, key, mergedValue));
        }else if(oldValue != null && mergedValue != null){
            listeners.forEach(l -> l.onChange(this, key, oldValue, mergedValue));
        }else if(oldValue != null && mergedValue == null){
            listeners.forEach(l -> l.onRemove(this, key, oldValue));
        }

        return mergedValue;
    }
}
