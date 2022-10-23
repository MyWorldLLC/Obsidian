package myworld.obsidian.display.skin;

import myworld.obsidian.display.skin.Provider;
import myworld.obsidian.display.skin.Variables;

public class ConstantProvider<T> extends Provider<T> {

    protected final T value;

    public ConstantProvider(String name, T value){
        super(name);
        this.value = value;
    }

    @Override
    public T get(Variables data, Class<T> type){
        return get(data);
    }

    @Override
    public T get(Variables data){
        return value;
    }

}
