package myworld.obsidian.behaviors;

import myworld.obsidian.scene.Component;

import java.util.function.Consumer;

public interface Behavior<T extends Component> {

    Behavior<T> apply(T c);

    default Behavior<T> with(Consumer<Behavior<T>> c){
        c.accept(this);
        return this;
    }

    default Behavior<T> with(Runnable r){
        r.run();
        return this;
    }

}
