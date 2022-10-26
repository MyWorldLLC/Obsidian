package myworld.obsidian.display.skin;

public class ConstantRule<T> implements StyleRule {

    protected final T value;

    public ConstantRule(T value){
        this.value = value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T evaluate(Variables data) {
        return value;
    }
}
