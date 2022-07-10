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

public class Listeners {

    public interface ListAddListener<T> {
        void onAdd(ListProperty<T> prop, int index, T element);
    }

    public interface ListItemChangeListener<T> {
        void onChange(ListProperty<T> prop, int index, T oldValue, T newValue);
    }

    public interface ListRemoveListener<T> {
        void onRemove(ListProperty<T> prop, int index, T element);
    }

    public static <T> ListChangeListener<T> list(Class<T> itemType,
            ListAddListener<T> onAdd,
            ListItemChangeListener<T> onChange,
            ListRemoveListener<T> onRemove){
        return new ListChangeListener<>() {

            @Override
            public void onAdd(ListProperty<T> prop, int index, T element){
                if(onAdd != null){
                    onAdd.onAdd(prop, index, element);
                }else{
                    ListChangeListener.super.onAdd(prop, index, element);
                }
            }

            @Override
            public void onChange(ListProperty<T> prop, int index, T oldValue, T newValue) {
                if(onChange != null){
                    onChange.onChange(prop, index, oldValue, newValue);
                }
            }

            @Override
            public void onRemove(ListProperty<T> prop, int index, T element){
                if(onRemove != null){
                    onRemove.onRemove(prop, index, element);
                }else{
                    ListChangeListener.super.onRemove(prop, index, element);
                }
            }
        };
    }

    public interface MapAddListener<K, V> {
        void onAdd(MapProperty<K, V> prop, K key, V value);
    }

    public interface MapItemChangeListener<K, V> {
        void onChange(MapProperty<K, V> prop, K key, V oldValue, V newValue);
    }

    public interface  MapRemoveListener<K, V> {
        void onRemove(MapProperty<K, V> prop, K key, V value);
    }

    public static <K, V> MapChangeListener<K, V> map(Class<K> keyType, Class<V> valueType,
                                                         MapAddListener<K, V> onAdd,
                                                         MapItemChangeListener<K, V> onChange,
                                                         MapRemoveListener<K, V> onRemove
                                                     ){
        return new MapChangeListener<>(){

            @Override
            public void onAdd(MapProperty<K, V> prop, K key, V value) {
                if(onAdd != null){
                    onAdd.onAdd(prop, key, value);
                }else{
                    MapChangeListener.super.onAdd(prop, key, value);
                }
            }

            @Override
            public void onChange(MapProperty<K, V> prop, K key, V oldValue, V newValue) {
                if(onChange != null){
                    onChange.onChange(prop, key, oldValue, newValue);
                }
            }

            @Override
            public void onRemove(MapProperty<K, V> prop, K key, V value) {
                if(onRemove != null){
                    onRemove.onRemove(prop, key, value);
                }else{
                    MapChangeListener.super.onRemove(prop, key, value);
                }
            }
        };

    }
}
