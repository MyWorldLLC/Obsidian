package myworld.obsidian.util;

import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Effect;

import java.util.function.Consumer;

public class EffectTimer implements Effect {

    public static final double DEFAULT_PERIOD = 1.0;

    protected final ValueProperty<Boolean> enabled;

    protected final ValueProperty<Double> period;
    protected final ValueProperty<Double> accumulatedTime;

    protected final ListProperty<Consumer<Double>> listeners;

    public EffectTimer(){
        enabled = new ValueProperty<>(true);
        this.period = new ValueProperty<>(DEFAULT_PERIOD);
        accumulatedTime = new ValueProperty<>(0.0);
        listeners = new ListProperty<>();
    }

    public ValueProperty<Boolean> enabled(){
        return enabled;
    }

    public ValueProperty<Double> period(){
        return period;
    }

    public ValueProperty<Double> accumulatedTime(){
        return accumulatedTime;
    }

    public ListProperty<Consumer<Double>> listeners(){
        return listeners;
    }

    @Override
    public void update(Consumer<Effect> done, double tpf){
        if(enabled.get(false)){
            var current = accumulatedTime.get() + tpf;
            if(current >= period.get()){
                listeners.forEach(l -> l.accept(current));
                reset();
            }else{
                accumulatedTime.set(current);
            }
        }
    }

    public void reset(){
        accumulatedTime.set(0.0);
    }

}
