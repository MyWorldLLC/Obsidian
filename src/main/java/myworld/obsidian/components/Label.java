package myworld.obsidian.components;

import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.Text;

public class Label extends Component {

    public static final String COMPONENT_STYLE_NAME = "Label";

    public static final String TEXT_DATA_NAME = "text";

    protected final ValueProperty<String> text;
    protected final ValueProperty<StyleClass> style;

    public Label(){
        this(null, null);
    }

    public Label(String text){
        this(text, null);
    }

    public Label(String text, StyleClass style){
        styleName.set(COMPONENT_STYLE_NAME);

        this.text = new ValueProperty<>();
        this.style = new ValueProperty<>();

        this.text.set(text);
        this.style.set(style);

        renderVars.put(TEXT_DATA_NAME, () -> Text.styled(this.text.get(), this.style.get()));
    }

    public ValueProperty<String> text(){
        return text;
    }

    public ValueProperty<StyleClass> style(){
        return style;
    }

}
