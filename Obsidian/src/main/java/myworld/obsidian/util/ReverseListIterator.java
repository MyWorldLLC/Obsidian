package myworld.obsidian.util;

import java.util.Iterator;
import java.util.ListIterator;

public class ReverseListIterator<E> implements Iterator<E> {

    protected final ListIterator<E> it;

    public ReverseListIterator(ListIterator<E> delegate){
        it = delegate;
    }

    @Override
    public boolean hasNext() {
        return it.hasPrevious();
    }

    @Override
    public E next() {
        return it.previous();
    }

    @Override
    public void remove(){
        it.remove();
    }

}
