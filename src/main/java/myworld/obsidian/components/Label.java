package myworld.obsidian.components;

import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.display.Colors;
import myworld.obsidian.display.TextRuler;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.Text;
import myworld.obsidian.text.TextStyle;

public class Label extends Component {

    public static final String COMPONENT_STYLE_NAME = "Label";
    public static final String DEFAULT_FONT_FAMILY = "Clear Sans";
    public static final TextStyle DEFAULT_FONT_STYLE = TextStyle.NORMAL;
    public static final Float DEFAULT_FONT_SIZE = 14f;
    public static final ColorRGBA DEFAULT_TEXT_COLOR = Colors.BLACK;

    public static final String TEXT_DATA_NAME = "text";
    public static final String FONT_FAMILY_DATA_NAME = "fontFamily";
    public static final String FONT_STYLE_DATA_NAME = "fontStyle";
    public static final String FONT_SIZE_DATA_NAME = "fontSize";
    public static final String TEXT_COLOR_DATA_NAME = "color";

    protected final ValueProperty<String> text;
    protected final ValueProperty<String> fontFamily;
    protected final ValueProperty<TextStyle> fontStyle;
    protected final ValueProperty<Float> fontSize;
    protected final ValueProperty<ColorRGBA> color;
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

        fontFamily = new ValueProperty<>(DEFAULT_FONT_FAMILY);
        fontStyle = new ValueProperty<>(DEFAULT_FONT_STYLE);
        fontSize = new ValueProperty<>(DEFAULT_FONT_SIZE);
        color = new ValueProperty<>(DEFAULT_TEXT_COLOR);

        this.text.set(text);
        this.style.set(style);

        renderVars.put(TEXT_DATA_NAME, () -> Text.styled(this.text.get(), this.style.get()));
        renderVars.put(FONT_FAMILY_DATA_NAME, fontFamily);
        renderVars.put(FONT_STYLE_DATA_NAME, fontStyle);
        renderVars.put(FONT_SIZE_DATA_NAME, fontSize);
        renderVars.put(TEXT_COLOR_DATA_NAME, color);
    }

    public ValueProperty<String> text(){
        return text;
    }

    public ValueProperty<StyleClass> style(){
        return style;
    }

    public ValueProperty<String> fontFamily(){
        return fontFamily;
    }

    public ValueProperty<TextStyle> fontStyle(){
        return fontStyle;
    }

    public ValueProperty<Float> fontSize(){
        return fontSize;
    }

    public ValueProperty<ColorRGBA> color(){
        return color;
    }

    public TextRuler getRuler(){
        return ui.get().getDisplay().getTextRuler(fontFamily.get(), fontStyle.get(), fontSize.get());
    }

}
