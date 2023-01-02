package myworld.obsidian.components;

import myworld.obsidian.events.scene.ButtonEvent;
import myworld.obsidian.events.scene.CheckboxEvent;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.properties.ValueProperty;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Checkbox extends Button {

    public static final String COMPONENT_STYLE_NAME = "Checkbox";
    public static final String CHECKED_DATA_NAME = "checked";

    public final ValueProperty<Boolean> checked;

    public Checkbox(){
        styleName.set(COMPONENT_STYLE_NAME);
        checked = new ValueProperty<>(false);
        layout.preferredSize(Distance.pixels(15), Distance.pixels(15));

        dispatcher.subscribe(ButtonEvent.class, ButtonEvent::isClicked, evt -> {
            checked.setWith(c -> !c);
            dispatcher.dispatch(new CheckboxEvent(this, checked.get()));
        });

        renderVars.put(CHECKED_DATA_NAME, checked);
    }

    public Checkbox addCheckListener(Consumer<CheckboxEvent> listener){
        addCheckListener(null, listener);
        return this;
    }

    public Checkbox addCheckListener(Predicate<CheckboxEvent> filter, Consumer<CheckboxEvent> listener){
        dispatcher.subscribe(CheckboxEvent.class, filter, listener);
        return this;
    }

    public Checkbox onCheck(Consumer<CheckboxEvent> listener){
        return addCheckListener(CheckboxEvent::isChecked, listener);
    }

    public Checkbox onUncheck(Consumer<CheckboxEvent> listener){
        return addCheckListener(CheckboxEvent::isUnchecked, listener);
    }

    public Checkbox onChange(Consumer<CheckboxEvent> listener){
        addCheckListener(listener);
        return this;
    }

    public ValueProperty<Boolean> checked(){
        return checked;
    }
}
