package myworld.obsidian.behaviors;

import myworld.obsidian.scene.Component;

public interface RemovableBehavior<T extends Component> extends Behavior<T> {

    Behavior<T> remove(T c);

}
