package myworld.obsidian.display.skin;

public class Provider<T> {

    protected final String name;

    public Provider(String name){
        this.name = name;
    }

    public T get(Variables data, Class<T> type){
        return data.get(name, type);
    }

    @SuppressWarnings("unchecked")
    public T get(Variables data){
        return (T) data.get(name);
    }
}
