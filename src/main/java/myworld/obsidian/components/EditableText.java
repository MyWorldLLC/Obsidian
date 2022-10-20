package myworld.obsidian.components;

import myworld.obsidian.events.CharacterEvent;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.layout.LayoutDimension;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;

public class EditableText extends Component {

    public static final String COMPONENT_STYLE_NAME = "EditableText";

    protected final Label label;
    protected final StringBuilder builder;
    protected final ValueProperty<Boolean> editable;

    public EditableText(){
        this(null);
    }

    public EditableText(String style){
        this("", style);
    }

    public EditableText(String initial, String style){
        styleName.set(COMPONENT_STYLE_NAME);
        label = new Label(initial, style);
        label.layout().preferredSize(LayoutDimension.pixels(100), LayoutDimension.pixels(100));
        addChild(label);

        builder = new StringBuilder();
        editable = new ValueProperty<>(true);

        dispatcher.subscribe(CharacterEvent.class, e -> editable.get(), evt -> append(evt.getCharacters()));
    }

    public Label getLabel(){
        return label;
    }

    public ValueProperty<String> style(){
        return label.style();
    }

    public ValueProperty<Boolean> editable(){
        return editable;
    }

    public void append(String s){
        builder.append(s);
        label.text().set(builder.toString());
    }

    public void append(char[] characters){
        builder.append(characters);
        label.text().set(builder.toString());
    }
}
