package myworld.obsidian.components.text;

import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.display.Colors;
import myworld.obsidian.display.TextRuler;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.Text;
import myworld.obsidian.text.TextStyle;
import myworld.obsidian.util.Range;

public class TextDisplay extends Component {

    public static final String COMPONENT_STYLE_NAME = "TextDisplay";
    public static final String DEFAULT_FONT_FAMILY = "Clear Sans";
    public static final TextStyle DEFAULT_FONT_STYLE = TextStyle.NORMAL;
    public static final Float DEFAULT_FONT_SIZE = 14f;
    public static final ColorRGBA DEFAULT_TEXT_COLOR = Colors.BLACK;
    public static final ColorRGBA DEFAULT_SELECTION_COLOR = ColorRGBA.of("#25EFF988");

    public static final String TEXT_DATA_NAME = "text";
    public static final String FONT_FAMILY_DATA_NAME = "fontFamily";
    public static final String FONT_STYLE_DATA_NAME = "fontStyle";
    public static final String FONT_SIZE_DATA_NAME = "fontSize";
    public static final String TEXT_COLOR_DATA_NAME = "color";
    public static final String SHOW_HIGHLIGHT_DATA_NAME = "showHighlight";
    public static final String HIGHLIGHT_COLOR_DATA_NAME = "highlightColor";
    public static final String HIGHLIGHT_POS_DATA_NAME = "highlightPos";
    public static final String HIGHLIGHT_WIDTH_DATA_NAME = "highlightWidth";
    public static final String HIGHLIGHT_HEIGHT_DATA_NAME = "highlightHeight";

    protected final ValueProperty<String> text;
    protected final ValueProperty<String> fontFamily;
    protected final ValueProperty<TextStyle> fontStyle;
    protected final ValueProperty<Float> fontSize;
    protected final ValueProperty<ColorRGBA> color;
    protected final ValueProperty<StyleClass> style;

    protected final ValueProperty<Boolean> selectable;
    protected final ValueProperty<Range<Integer>> selection;
    protected final ValueProperty<ColorRGBA> selectionColor;

    public TextDisplay(){
        this(null, null);
    }

    public TextDisplay(Text text){
        this(text.text(), text.style());
    }

    public TextDisplay(String text){
        this(text, null);
    }

    public TextDisplay(String text, StyleClass style){
        styleName.set(COMPONENT_STYLE_NAME);
        focusable.set(false);

        this.text = new ValueProperty<>();
        this.style = new ValueProperty<>();

        fontFamily = new ValueProperty<>(DEFAULT_FONT_FAMILY);
        fontStyle = new ValueProperty<>(DEFAULT_FONT_STYLE);
        fontSize = new ValueProperty<>(DEFAULT_FONT_SIZE);
        color = new ValueProperty<>(DEFAULT_TEXT_COLOR);

        selectable = new ValueProperty<>(true);
        selection = new ValueProperty<>();
        selectionColor = new ValueProperty<>(DEFAULT_SELECTION_COLOR);

        this.text.set(text);
        this.style.set(style);

        preLayout(() -> {
            var ruler = getRuler();
            layout.preferredSize(Distance.pixels(ruler.getWidth(text().get())), Distance.pixels(ruler.getLineHeight()));
        });

        renderVars.put(TEXT_DATA_NAME, () -> Text.styled(this.text.get(), this.style.get()));
        renderVars.put(FONT_FAMILY_DATA_NAME, fontFamily);
        renderVars.put(FONT_STYLE_DATA_NAME, fontStyle);
        renderVars.put(FONT_SIZE_DATA_NAME, fontSize);
        renderVars.put(TEXT_COLOR_DATA_NAME, color);
        renderVars.put(SHOW_HIGHLIGHT_DATA_NAME, () -> selectable.get() && selection.get() != null);
        renderVars.put(HIGHLIGHT_COLOR_DATA_NAME, selectionColor);
        renderVars.put(HIGHLIGHT_POS_DATA_NAME, () -> {
            if(selection.get() != null){
                return getRuler().getXPositions(text().get())[clampedStringIndex(selection.get().start())];
            }
            return 0f;
        });
        renderVars.put(HIGHLIGHT_WIDTH_DATA_NAME, () -> {
            if(selection.get() != null){
                if(selection.get().end().equals(selection.get().start())){
                    return 0f;
                }

                var ruler = getRuler();
                var widths = ruler.getWidths(text().get());
                var positions = ruler.getXPositions(text().get());
                return (widths[clampedStringIndex(selection.get().end() - 1)]
                        + positions[clampedStringIndex(selection.get().end() - 1)])
                        - positions[clampedStringIndex(selection.get().start())];
            }
            return 0f;
        });
        renderVars.put(HIGHLIGHT_HEIGHT_DATA_NAME, () -> getRuler().getLineHeight());
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

    public ValueProperty<Boolean> selectable(){
        return selectable;
    }

    public ValueProperty<Range<Integer>> selection(){
        return selection;
    }

    public ValueProperty<ColorRGBA> selectionColor(){
        return selectionColor;
    }

    public TextRuler getRuler(){
        return ui.get().getDisplay().getTextRuler(fontFamily.get(), fontStyle.get(), fontSize.get());
    }

    protected int clampedStringIndex(int index){
        return Range.clamp(0, index, Math.max(0, text().get().length() - 1));
    }

}
