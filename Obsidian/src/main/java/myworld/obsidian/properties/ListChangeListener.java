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

public interface ListChangeListener<T> {

    default void onAdd(ListProperty<T> prop, int index, T element){
        onChange(prop, index, null, element);
    }

    void onChange(ListProperty<T> prop, int index, T oldValue, T newValue);

    default void onRemove(ListProperty<T> prop, int index, T element){
        onChange(prop, index, element, null);
    }

}
