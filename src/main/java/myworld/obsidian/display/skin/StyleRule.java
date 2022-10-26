package myworld.obsidian.display.skin;

import java.util.function.Function;

public interface StyleRule {

    <T> T evaluate(Variables data);

    static <T> ConstantRule<T> constant(T value){
        return new ConstantRule<>(value);
    }

    static StyleRule of(Function<Variables, ?> factory){
        return new StyleRule() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> T evaluate(Variables v) {
                return (T) factory.apply(v);
            }
        };
    }

}
