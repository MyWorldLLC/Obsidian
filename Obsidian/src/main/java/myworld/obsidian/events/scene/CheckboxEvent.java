package myworld.obsidian.events.scene;

import myworld.obsidian.scene.Component;

public class CheckboxEvent extends ActionEvent {

    protected final boolean checked;

    public CheckboxEvent(Component component, boolean checked) {
        super(component);
        this.checked = checked;
    }

    public boolean isChecked(){
        return checked;
    }
}
