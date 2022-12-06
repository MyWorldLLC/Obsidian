package myworld.obsidian.components.text;

import myworld.obsidian.events.input.CharacterEvent;
import myworld.obsidian.events.input.KeyEvent;
import myworld.obsidian.events.input.MouseButtonEvent;
import myworld.obsidian.events.input.MouseMoveEvent;
import myworld.obsidian.events.scene.FocusEvent;
import myworld.obsidian.input.Key;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;

import static myworld.obsidian.events.dispatch.EventFilters.*;

public class TextField extends Component {

    public static TextField plain(){
        return new TextField();
    }

    public static TextField password(char mask){
        var textField = new TextField();
        textField.mask().set(mask);
        return textField;
    }

    public static final String DEFAULT_STYLE_NAME = "TextField";
    protected final EditableTextDisplay text;
    protected final ValueProperty<Character> mask;

    public TextField(){
        styleName.set(DEFAULT_STYLE_NAME);
        text = new EditableTextDisplay();
        addChild(text);

        mask = new ValueProperty<>();
        text.editor().set(new TextFieldEditor(mask));
        text.focusable().set(false);

        dispatcher.subscribe(CharacterEvent.class, e -> text.editable().get() && !e.getManager().isControlDown(),
                evt -> text.insert(evt.getCharacters()));

        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT), evt -> text.cursorBackward());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT), evt -> text.cursorForward());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_SHIFT, Key.LEFT), evt -> text.adjustHighlight(false));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_SHIFT, Key.RIGHT), evt -> text.adjustHighlight(true));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.BACKSPACE), evt -> text.delete());

        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_CONTROL, Key.KEY_C), evt -> text.copy());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_CONTROL, Key.KEY_X), evt -> text.cut());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_CONTROL, Key.KEY_V), evt -> text.paste());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT_CONTROL, Key.KEY_C), evt -> text.copy());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT_CONTROL, Key.KEY_X), evt -> text.cut());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT_CONTROL, Key.KEY_V), evt -> text.paste());

        dispatcher.subscribe(MouseButtonEvent.class, mousePressed(), evt ->{
            text.startPointerSelect(evt.getX());
        });

        dispatcher.subscribe(MouseMoveEvent.class, e -> e.getManager().isDown(MouseButton.PRIMARY), evt -> {
            text.midPointerSelect(evt.getX(), evt.getY());
        });

        dispatcher.subscribe(MouseButtonEvent.class, mouseReleased(), evt -> {
            text.endPointerSelect(evt.getX());
        });

        dispatcher.subscribe(FocusEvent.class, e -> e.lostFocus(this), evt -> text.clearSelection());
    }

    public ValueProperty<Character> mask(){
        return mask;
    }

    public EditableTextDisplay text(){
        return text;
    }

    public void insert(String s){
        text.insert(s);
    }

    public void delete(){
        text.delete();
    }

    protected static class TextFieldEditor implements Editor {

        protected final ValueProperty<Character> mask;
        protected final StringBuilder builder = new StringBuilder();

        public TextFieldEditor(ValueProperty<Character> mask){
            this.mask = mask;
        }

        @Override
        public void insert(int index, String s) {
            builder.insert(index, s);
        }

        @Override
        public void delete(int start, int end) {
            builder.delete(start, end);
        }

        @Override
        public String substring(int start, int end) {
            return getContents(start, end);
        }

        @Override
        public int length() {
            return builder.length();
        }

        @Override
        public String toString(){
            return getContents();
        }

        protected String getContents(){
            return getContents(0, builder.length());
        }

        protected String getContents(int start, int end){
            return mask.get() == null ? builder.substring(start, end) : String.valueOf(mask.get()).repeat(end - start);
        }
    }

}