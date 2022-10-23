package myworld.obsidian.components;

import myworld.obsidian.events.CharacterEvent;
import myworld.obsidian.events.KeyEvent;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.input.Key;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;

import static myworld.obsidian.events.dispatch.EventFilters.*;

public class EditableText extends Component {

    public static final String COMPONENT_STYLE_NAME = "EditableText";
    public static final String LINE_HEIGHT_VAR_NAME = "lineHeight";
    public static final String CURSOR_OFFSET_VAR_NAME = "cursorOffset";
    public static final String CURSOR_VISIBLE_VAR_NAME = "cursorVisible";

    protected final Label label;
    protected final StringBuilder builder;
    protected final ValueProperty<Boolean> editable;
    protected final ValueProperty<Integer> cursorPos;

    public EditableText(){
        this(null);
    }

    public EditableText(String style){
        this("", style);
    }

    public EditableText(String initial, String style){
        styleName.set(COMPONENT_STYLE_NAME);
        label = new Label(initial, style);
        label.layout().preferredSize(Distance.pixels(100), Distance.pixels(100));
        addChild(label);

        builder = new StringBuilder();
        editable = new ValueProperty<>(true);
        cursorPos = new ValueProperty<>(0);

        dispatcher.subscribe(CharacterEvent.class, e -> editable.get(), evt -> insert(evt.getCharacters()));
        dispatcher.subscribe(KeyEvent.class, keyPressed(Key.LEFT), evt -> cursorBackward());
        dispatcher.subscribe(KeyEvent.class, keyPressed(Key.RIGHT), evt -> cursorForward());
        dispatcher.subscribe(KeyEvent.class, keyPressed(Key.BACKSPACE), evt -> deletePrevious());

        renderVars.put(CURSOR_VISIBLE_VAR_NAME, focused());
        renderVars.put(CURSOR_OFFSET_VAR_NAME, () -> cursorPos().get() * 5); // TODO - measure cursor offset based on current text

        preRender(() -> label.text().set(builder.toString()));
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

    public ValueProperty<Integer> cursorPos(){
        return cursorPos;
    }

    public void insert(String s){
        builder.insert(cursorPos.get(), s);
        cursorPos.setWith(c -> c + s.length());
    }

    public void insert(char[] characters){
        builder.insert(cursorPos.get(), characters);
        cursorPos.setWith(c -> c + characters.length);
    }

    public void deletePrevious(){
        if(cursorPos.get() > 0){
            builder.deleteCharAt(cursorPos.get() - 1);
            cursorBackward();
        }
    }

    public void deleteCurrent(){
        if(cursorPos.get() < builder.length()){
            builder.deleteCharAt(cursorPos.get());
        }
    }

    public void cursorForward(){
        moveCursor(cursorPos.get() + 1);
    }

    public void cursorBackward(){
        moveCursor(cursorPos().get() - 1);
    }

    public void moveCursor(int pos){
        pos = Math.max(0, pos);
        pos = Math.min(pos, builder.length());
        cursorPos.set(pos);
    }

}