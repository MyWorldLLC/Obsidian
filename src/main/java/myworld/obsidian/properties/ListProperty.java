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
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ListProperty<T> extends Property<ListChangeListener<T>> implements List<T>  {

    protected final List<T> values;

    public ListProperty(){
        this(0);
    }

    public ListProperty(int initialCapacity){
        values = Collections.synchronizedList(new ArrayList<>(initialCapacity));
    }

    public ListProperty(T[] initialValues){
        values = Collections.synchronizedList(new ArrayList<>(Arrays.asList(initialValues)));
    }

    private ListProperty(List<T> backedBy){
        values = backedBy;
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
    public boolean contains(Object o) {
        return values.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return listIterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        values.forEach(action);
    }

    @Override
    public Object[] toArray() {
        return values.toArray();
    }

    @Override
    public <V> V[] toArray(V[] a) {
        return values.toArray(a);
    }

    @Override
    public <V> V[] toArray(IntFunction<V[]> generator) {
        return values.toArray(generator);
    }

    @Override
    public boolean add(T t) {
        var added = values.add(t);
        listeners.forEach(l -> l.onAdd(this, size() - 1, t));
        return added;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        var removed = values.remove(o);
        if(removed){
            listeners.forEach(l -> l.onRemove(this, -1, (T)o));
        }
        return removed;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return values.contains(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        c.forEach(this::add);
        return c.size() > 0;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        c.forEach(this::add);
        return c.size() > 0;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        var changed = new ValueProperty<Boolean>();
        c.forEach(t -> {
            var contained = remove(t);
            if(contained){
                changed.set(true);
            }
        });
        return changed.get();
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        var changed = new ValueProperty<>(false);
        var toRemove = new ArrayList<T>();
        for(int i = 0; i < values.size(); i++){
            T value = values.get(i);
            var matches = filter.test(value);
            if(matches){
                changed.set(true);
                int finalI = i;
                listeners.forEach(l -> l.onRemove(this, finalI, value));
                toRemove.add(value);
            }
        }
        values.removeAll(toRemove);
        return changed.get();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return removeIf(t -> !c.contains(t));
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        for(int i = 0; i < values.size(); i++){
            var oldValue = values.get(i);
            var newValue = operator.apply(oldValue);
            values.set(i, newValue);
            int finalI = i;
            listeners.forEach(l -> l.onChange(this, finalI, oldValue, newValue));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super T> c) {
        var former = values.toArray();
        values.sort(c);
        for(int i = 0; i < values.size(); i++){
            if(values.get(i) != former[i]){
                int finalI = i;
                listeners.forEach(l -> l.onChange(this, finalI, (T)former[finalI], values.get(finalI)));
            }
        }
    }

    @Override
    public void clear() {
        for(int i = 0; i < values.size(); i++){
            int finalI = i;
            listeners.forEach(l -> l.onRemove(this, finalI, values.get(finalI)));
        }
        values.clear();
    }

    @Override
    public T get(int index) {
        return values.get(index);
    }

    @Override
    public T set(int index, T element) {
        var previous = values.set(index, element);
        listeners.forEach(l -> l.onChange(this, index, previous, element));
        return previous;
    }

    @Override
    public void add(int index, T element) {
        values.add(index, element);
        listeners.forEach(l -> l.onAdd(this, index, element));
    }

    @Override
    public T remove(int index) {
        var element = values.remove(index);
        listeners.forEach(l -> l.onRemove(this, index, element));
        return element;
    }

    @Override
    public int indexOf(Object o) {
        return values.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return values.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return new PropertyListIterator(values.listIterator());
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new PropertyListIterator(values.listIterator(index));
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        var next = new ListProperty<>(values.subList(fromIndex, toIndex));
        subscribeAll(next);
        return next;
    }

    @Override
    public Spliterator<T> spliterator() {
        return values.spliterator();
    }

    @Override
    public Stream<T> stream() {
        return values.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return values.parallelStream();
    }

    protected class PropertyListIterator implements ListIterator<T> {

        protected final ListIterator<T> delegate;

        public PropertyListIterator(ListIterator<T> delegate){
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public T next() {
            return delegate.next();
        }

        @Override
        public boolean hasPrevious() {
            return delegate.hasPrevious();
        }

        @Override
        public T previous() {
            return delegate.previous();
        }

        @Override
        public int nextIndex() {
            return delegate.nextIndex();
        }

        @Override
        public int previousIndex() {
            return delegate.previousIndex();
        }

        @Override
        public void remove() {
            listeners.forEach(l -> l.onRemove(ListProperty.this, nextIndex() - 1, values.get(nextIndex() - 1)));
            delegate.remove();
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            ListIterator.super.forEachRemaining(action);
        }

        @Override
        public void set(T t) {
            listeners.forEach(l -> l.onChange(ListProperty.this, nextIndex() - 1, values.get(nextIndex() - 1), t));
            delegate.set(t);
        }

        @Override
        public void add(T t) {
            listeners.forEach(l -> l.onAdd(ListProperty.this, nextIndex() - 1, t));
            delegate.add(t);
        }
    }

}
