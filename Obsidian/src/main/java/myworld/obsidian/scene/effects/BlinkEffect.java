package myworld.obsidian.scene.effects;

import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.util.EffectTimer;


public class BlinkEffect extends EffectTimer {


    protected final ValueProperty<Boolean> on;

    public BlinkEffect(){
        on = new ValueProperty<>(true);
        listeners().add(accumulated -> on.setWith(b -> !b));
    }

    public ValueProperty<Boolean> on(){
        return on;
    }
}
