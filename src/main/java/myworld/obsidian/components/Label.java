package myworld.obsidian.components;

import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.Text;

public class Label extends Component {

    public static final String COMPONENT_STYLE_NAME = "Label";

    public static final String TEXT_DATA_NAME = "text";

    protected final ValueProperty<String> text;
    protected final ValueProperty<String> style;

    public Label(){
        this(null, null);
    }

    public Label(String text){
        this(text, null);
    }

    public Label(String text, String style){
        styleName.set(COMPONENT_STYLE_NAME);

        this.text = new ValueProperty<>();
        this.style = new ValueProperty<>();

        this.text.addListener((p, o, n) -> updateForRender());
        this.style.addListener((p, o, n) -> updateForRender());

        this.text.set(text);
        this.style.set(style);
    }

    public ValueProperty<String> text(){
        return text;
    }

    public ValueProperty<String> style(){
        return style;
    }

    protected void updateForRender(){
        data.set(TEXT_DATA_NAME, Text.styled(text.get(), style.get()));
        // TODO - request layout width based on result of measured text
    }

}
