package myworld.obsidian.components;

import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.events.*;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.geometry.Point2D;
import myworld.obsidian.input.Key;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.util.Range;

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
    protected final ValueProperty<Point2D> dragStart;

    public EditableText(){
        this(null);
    }

    public EditableText(StyleClass style){
        this("", style);
    }

    public EditableText(String initial, StyleClass style){
        styleName.set(COMPONENT_STYLE_NAME);
        label = new Label(initial, style);
        label.layout().preferredSize(Distance.pixels(100), Distance.pixels(100));
        addChild(label);

        builder = new StringBuilder();
        editable = new ValueProperty<>(true);
        cursorPos = new ValueProperty<>(0);
        dragStart = new ValueProperty<>();

        dispatcher.subscribe(CharacterEvent.class, e -> editable.get() && !e.getManager().isControlDown(),
                evt -> insert(evt.getCharacters()));

        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT), evt -> cursorBackward());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT), evt -> cursorForward());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_SHIFT, Key.LEFT), evt -> adjustHighlight(false));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_SHIFT, Key.RIGHT), evt -> adjustHighlight(true));
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.BACKSPACE), evt -> delete());

        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_CONTROL, Key.KEY_C), evt -> copy());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_CONTROL, Key.KEY_X), evt -> cut());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.LEFT_CONTROL, Key.KEY_V), evt -> paste());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT_CONTROL, Key.KEY_C), evt -> copy());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT_CONTROL, Key.KEY_X), evt -> cut());
        dispatcher.subscribe(KeyEvent.class, accelerator(Key.RIGHT_CONTROL, Key.KEY_V), evt -> paste());

        dispatcher.subscribe(MouseButtonEvent.class, mousePressed(), evt ->{
            moveCursor(label.getRuler().getCharIndex(builder.toString(), label.localizeX(evt.getX())));
        });
        dispatcher.subscribe(MouseButtonEvent.class, mouseReleased(), evt -> {
            if(dragStart.get() == null){
                // If there's a selection, clear it
                clearSelection();
            }else{
                dragStart.set(null);
                positionCursor(evt);
            }

        });

        dispatcher.subscribe(MouseMoveEvent.class, e -> e.getManager().isDown(MouseButton.PRIMARY), evt ->{
            if(dragStart.get() == null){
                positionCursor(evt);
            }
        });

        dispatcher.subscribe(MouseMoveEvent.class, e -> e.getManager().isDown(MouseButton.PRIMARY),
                evt -> {

                    if(dragStart.get() == null){
                        dragStart.set(label.localize(evt.getPoint()));
                    }

                    var ruler = label.getRuler();
                    var s = builder.toString();

                    var startIndex = ruler.getCharIndex(s, dragStart.get().x());
                    var index = ruler.getCharIndex(s, label.localizeX(evt.getX()));

                    select(Math.min(startIndex, index), Math.max(startIndex, index));
                });

        dispatcher.subscribe(FocusEvent.class, e -> e.lostFocus(this), evt -> label.selection().set(null));

        renderVars.put(CURSOR_VISIBLE_VAR_NAME, () -> focused().get() && editable().get() && dragStart.get() == null);
        renderVars.put(CURSOR_OFFSET_VAR_NAME, () -> {
            var ruler = label.getRuler();

            var s = builder.toString();

            if(cursorPos.get() == 0){
                return 0f;
            }

            int limit = cursorPos.get();

            var offset = 0f;
            var positions = ruler.getXPositions(s);
            var widths = ruler.getWidths(s);

            offset = positions[limit - 1] + widths[limit - 1];
            var bounds = ui().get().getLayout().getLocalBounds(this);

            return Math.min(offset, bounds.width());
        });
        renderVars.put(LINE_HEIGHT_VAR_NAME, () -> label.getRuler()
                .getLineHeight());

        preRender(() -> label.text().set(builder.toString()));
    }

    public Label getLabel(){
        return label;
    }

    public ValueProperty<StyleClass> style(){
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
        clearSelection();
    }

    public void insert(char[] characters){
        builder.insert(cursorPos.get(), characters);
        cursorPos.setWith(c -> c + characters.length);
        clearSelection();
    }

    public void select(int start, int end){
        label.selection().set(new Range<>(start, end));
    }

    public void clearSelection(){
        label.selection().set(null);
    }

    public String copy(){
        return copy(false);
    }

    public String copy(boolean delete){
        var selection = label.selection().get();
        if(selection != null){
            var s = builder.substring(selection.start(), selection.end());
            if(delete){
                builder.delete(selection.start(), selection.end());
                moveCursor(selection.start());
                clearSelection();
            }
            ui().ifSet(ui -> ui.clipboard().ifSet(c -> c.toClipboard(s)));
            return s;
        }
        return null;
    }

    public String cut(){
        return copy(true);
    }

    public void paste(){
        ui().ifSet(ui -> ui.clipboard().ifSet(c -> {
            var s = c.fromClipboard();
            if(s != null){
                insert(s.toCharArray());
            }
        }));
    }

    public void delete(){
        var selection = label.selection().get();
        if(selection != null){
            builder.delete(selection.start(), selection.end());
            clearSelection();
            moveCursor(selection.start());
        }else{
            deletePrevious();
        }
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
        cursorForward(true);
    }

    public void cursorBackward(){
        cursorBackward(true);
    }

    public void cursorForward(boolean clearSelection){
        moveCursor(cursorPos.get() + 1);
        if(clearSelection){
            clearSelection();
        }
    }

    public void cursorBackward(boolean clearSelection){
        moveCursor(cursorPos().get() - 1);
        if(clearSelection){
            clearSelection();
        }
    }

    protected void adjustHighlight(boolean forward){
        var cursor = cursorPos.get();
        var nextCursor = forward ? cursor + 1 : cursor - 1;
        if(nextCursor < 0 || nextCursor > builder.length()){
            return;
        }

        var range = label.selection().get(new Range<>(cursor, cursor));

        if(forward){
            if(nextCursor > range.end()){
                range = new Range<>(range.start(), nextCursor);
            }else if(nextCursor > range.start()){
                range = new Range<>(nextCursor, range.end());
            }
        }else{
            if(nextCursor > range.start() && nextCursor < range.end()){
                range = new Range<>(range.start(), nextCursor);
            }else if(nextCursor < range.start()){
                range = new Range<>(nextCursor, range.end());
            }
        }
        label.selection().set(range);

        moveCursor(nextCursor);
    }

    public void moveCursor(int pos){
        pos = Math.max(0, pos);
        pos = Math.min(pos, builder.length());
        cursorPos.set(pos);
    }

    protected void positionCursor(BaseMouseEvent evt){
        moveCursor(calculateCursorIndex(label.localizeX(evt.getX())));
    }

    protected int calculateCursorIndex(float xCoord){
        return label.getRuler().getCharIndex(builder.toString(), xCoord);
    }

}
