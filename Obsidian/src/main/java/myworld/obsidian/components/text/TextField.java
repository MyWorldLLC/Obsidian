package myworld.obsidian.components.text;

import myworld.obsidian.events.input.CharacterEvent;
import myworld.obsidian.events.input.KeyEvent;
import myworld.obsidian.events.input.MouseButtonEvent;
import myworld.obsidian.events.input.MouseMoveEvent;
import myworld.obsidian.events.scene.FocusEvent;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.input.Key;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.layout.*;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;

import static myworld.obsidian.events.dispatch.EventFilters.*;
import static myworld.obsidian.events.dispatch.EventHandlers.*;

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
        text.editable().set(false); // Start uneditable, until we get focus
        text.focusable().set(false);

        // Default minimum width to 100px.
        layout.minWidth().set(Distance.pixels(100));

        layout.justifyContent().set(ItemJustification.FLEX_START);
        layout.padding().set(new Offsets(Distance.pixels(2), Layout.ZERO, Distance.pixels(2), Layout.ZERO));
        clipChildren.set(true);

        dispatcher.subscribe(CharacterEvent.class, e -> text.editable().get() && !e.getManager().isControlDown(),
                consuming(evt -> text.insert(evt.getCharacters())));

        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT), consuming(evt -> text.cursorBackward()));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT), consuming(evt -> text.cursorForward()));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_SHIFT, Key.LEFT), consuming(evt -> text.adjustHighlight(false)));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_SHIFT, Key.RIGHT), consuming(evt -> text.adjustHighlight(true)));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.BACKSPACE), consuming(evt -> text.delete()));

        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_CONTROL, Key.KEY_C), consuming(evt -> text.copy()));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_CONTROL, Key.KEY_X), consuming(evt -> text.cut()));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_CONTROL, Key.KEY_V), consuming(evt -> text.paste()));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT_CONTROL, Key.KEY_C), consuming(evt -> text.copy()));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT_CONTROL, Key.KEY_X), consuming(evt -> text.cut()));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT_CONTROL, Key.KEY_V), consuming(evt -> text.paste()));

        dispatcher.subscribe(MouseButtonEvent.class, mousePressed(), evt ->{
            text.startPointerSelect(evt.getX());
            evt.consume();
        });

        dispatcher.subscribe(MouseMoveEvent.class, e -> e.getManager().isDown(MouseButton.PRIMARY), evt -> {
            if(!focused().get()){
                ui.get().requestFocus(this);
            }
            text.midPointerSelect(evt.getX(), evt.getY());
            evt.consume();
        });

        dispatcher.subscribe(MouseButtonEvent.class, mouseReleased(), evt -> {
            text.endPointerSelect(evt.getX());
            evt.consume();
        });

        dispatcher.subscribe(FocusEvent.class, e -> e.gainedFocus(this), evt -> text.editable().set(true));
        dispatcher.subscribe(FocusEvent.class, e -> e.lostFocus(this), evt -> {
            text.editable().set(false);
            text.clearSelection();
        });
    }

    public ValueProperty<Character> mask(){
        return mask;
    }

    public EditableTextDisplay editableText(){
        return text;
    }

    public void insert(String s){
        text.insert(s);
    }

    public void delete(){
        text.delete();
    }

    public String getValue(){
        return text.getValue();
    }

    public void setValue(String s){
        text.setText(s);
    }

    protected static class TextFieldEditor implements Editor {

        protected final ValueProperty<Character> mask;
        protected final StringBuilder builder = new StringBuilder();

        protected boolean needsSync = false;

        public TextFieldEditor(ValueProperty<Character> mask){
            this.mask = mask;
        }

        @Override
        public void insert(int index, String s) {
            needsSync = true;
            builder.insert(index, s);
        }

        @Override
        public void delete(int start, int end) {
            needsSync = true;
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

        @Override
        public boolean pendingSync(){
            return needsSync;
        }

        @Override
        public String sync(){
            needsSync = false;
            return toString();
        }

        protected String getContents(){
            return getContents(0, builder.length());
        }

        protected String getContents(int start, int end){
            return mask.get() == null ? builder.substring(start, end) : String.valueOf(mask.get()).repeat(end - start);
        }
    }

}
