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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ValueProperty<T> extends Property<ValueChangeListener<T>> implements Supplier<T> {
    protected final AtomicReference<T> value;

    public ValueProperty(){
        value = new AtomicReference<>();
        bindListener = (p, o, n) -> value.set(n);
    }

    public ValueProperty(T initialValue){
        value = new AtomicReference<>(initialValue);
    }

    public void set(T value){
        var oldValue = this.value.get();
        this.value.set(value);
        listeners.forEach(l -> l.onChange(this, oldValue, value));
    }

    public void setWith(Function<T, T> provider){
        set(provider.apply(get()));
    }

    public boolean ifSet(Consumer<T> c){
        var v = get();
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
        var v = get();
        return v != null ? v : ifNull;
    }

    public boolean isSet(){
        return get() != null;
    }

    public boolean is(T other){
        return Objects.equals(get(), other);
    }

}
