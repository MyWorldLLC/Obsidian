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

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ValueProperty<T> extends Property<ValueChangeListener<T>> {
    protected final AtomicReference<T> value;

    public ValueProperty(){
        value = new AtomicReference<>();
    }

    public ValueProperty(T initialValue){
        value = new AtomicReference<>(initialValue);
    }

    public void set(T value){
        var oldValue = this.value.get();
        this.value.set(value);
        listeners.forEach(l -> l.onChange(this, oldValue, value));
    }

    public boolean ifSet(Consumer<T> c){
        var v = value.get();
        if(v != null){
            c.accept(v);
            return true;
        }
        return false;
    }

    public T get(){
        return value.get();
    }

    public T get(T ifNull){
        var v = value.get();
        return v != null ? v : ifNull;
    }

}
