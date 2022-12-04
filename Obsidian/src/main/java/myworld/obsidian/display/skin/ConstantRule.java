package myworld.obsidian.display.skin;

public record ConstantRule<T>(T value) implements StyleRule {

    @Override
    @SuppressWarnings("unchecked")
    public T evaluate(Variables data) {
        return value;
    }

}
