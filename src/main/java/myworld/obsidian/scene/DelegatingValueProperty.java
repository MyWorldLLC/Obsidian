package myworld.obsidian.scene;

import myworld.obsidian.properties.ValueProperty;

import java.util.function.Function;

public class DelegatingValueProperty<C extends Component, T> extends ValueProperty<T> {

    protected final ValueProperty<C> parent;
    protected final Function<C, ValueProperty<T>> delegate;

    public DelegatingValueProperty(ValueProperty<C> parent, Function<C, ValueProperty<T>> delegate){
        this.parent = parent;
        this.delegate = delegate;
    }

    public T get(){
        if(super.get() != null){
            return super.get();
        }else if(parent.get() != null){
            return delegate.apply(parent.get()).get();
        }

        return null;
    }
}
