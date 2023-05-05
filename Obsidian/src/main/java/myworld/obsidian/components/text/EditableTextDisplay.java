package myworld.obsidian.components.text;

import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.display.TextRuler;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.geometry.Point2D;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.scene.effects.BlinkEffect;
import myworld.obsidian.text.TextStyle;
import myworld.obsidian.util.Range;


public class EditableTextDisplay extends Component {

    public static final String COMPONENT_STYLE_NAME = "EditableTextDisplay";
    public static final String LINE_HEIGHT_VAR_NAME = "lineHeight";
    public static final String CURSOR_OFFSET_VAR_NAME = "cursorOffset";
    public static final String CURSOR_VISIBLE_VAR_NAME = "cursorVisible";

    protected final TextDisplay label;
    protected final ValueProperty<Editor> editor;
    protected final ValueProperty<Boolean> editable;
    protected final ValueProperty<Integer> cursorPos;
    protected final ValueProperty<Point2D> dragStart;

    protected final BlinkEffect blink;

    public EditableTextDisplay(){
        this(null);
    }

    public EditableTextDisplay(StyleClass style){
        styleName.set(COMPONENT_STYLE_NAME);
        focusable.set(false);
        label = new TextDisplay("", style);
        // Always leave a few pixels to display a cursor for an empty text area
        layout.minWidth().set(Distance.pixels(5));
        addChild(label);

        preLayout(() -> {
            label.calculatePreferredSize();
            layout.preferredSize(label.layout().preferredWidth().get(), label.layout().preferredHeight().get());
        });

        editor = new ValueProperty<>();
        editable = new ValueProperty<>(true);
        cursorPos = new ValueProperty<>(0);
        dragStart = new ValueProperty<>();

        blink = new BlinkEffect();
        blink.period().set(0.75);
        addEffect(blink);
        // Keep cursor on as long as it's moving
        cursorPos.addListener((prop, oldValue, newValue) -> {
            blink.on().set(true);
            blink.reset();
        });

        renderVars.put(CURSOR_VISIBLE_VAR_NAME, () ->
                editable().get() &&
                dragStart.get() == null &&
                blink.on().get(true));

        renderVars.put(CURSOR_OFFSET_VAR_NAME, () -> {
            var ruler = label.getRuler();

            var s = editor().get().toString();

            if(cursorPos.get() == 0){
                return 0f;
            }

            int limit = cursorPos.get();

            var offset = 0f;
            var positions = ruler.getXPositions(s);
            var widths = ruler.getWidths(s);

            offset = positions[limit - 1] + widths[limit - 1];
            var bounds = this.getLocalBounds();

            return Math.min(offset, bounds.width());
        });
        renderVars.put(LINE_HEIGHT_VAR_NAME, () -> label.getRuler()
                .getLineHeight());

        preLayout(() -> {
            var editor = editor().get();
            if(editor != null && editor.pendingSync()){
                label.text().set(editor.sync());
            }
        });

    }

    public ValueProperty<String> text(){
        return label.text();
    }

    public ValueProperty<StyleClass> style(){
        return label.style();
    }

    public ValueProperty<Editor> editor(){
        return editor;
    }

    public ValueProperty<Boolean> editable(){
        return editable;
    }

    public ValueProperty<Integer> cursorPos(){
        return cursorPos;
    }

    public ValueProperty<String> fontFamily(){
        return label.fontFamily();
    }

    public ValueProperty<TextStyle> fontStyle(){
        return label.fontStyle();
    }

    public ValueProperty<Float> fontSize(){
        return label.fontSize();
    }

    public ValueProperty<ColorRGBA> color(){
        return label.color();
    }

    public ValueProperty<Boolean> selectable(){
        return label.selectable();
    }

    public ValueProperty<ColorRGBA> selectionColor(){
        return label.selectionColor();
    }

    public BlinkEffect blink(){
        return blink;
    }

    public TextRuler getRuler(){
        return label.getRuler();
    }

    public void insert(String s){
        editor.get().insert(cursorPos.get(), s);
        cursorPos.setWith(c -> c + s.length());
        clearSelection();
    }

    public void insert(char[] characters){
        insert(String.copyValueOf(characters));
    }

    public void select(int start, int end){
        label.selection().set(new Range<>(start, end));
    }

    public void clearSelection(){
        label.selection().set(null);
    }

    public void selectAll(){
        select(0, editor().get().length());
    }

    public Range<Integer> selection(){
        return label.selection().get();
    }

    public void startPointerSelect(int screenX){
        moveCursor(label.getRuler().getCharIndex(editor().get().toString(), label.localizeX(screenX)));
    }

    public void midPointerSelect(int screenX, int screenY){
        if(dragStart.get() == null){
            positionCursor(screenX);
            dragStart.set(label.localize(new Point2D(screenX, screenY)));
        }

        var ruler = label.getRuler();
        var s = editor().get().toString();

        var startIndex = ruler.getCharIndex(s, dragStart.get().x());
        var index = ruler.getCharIndex(s, label.localizeX(screenX));

        select(Math.min(startIndex, index), Math.max(startIndex, index));
    }

    public void endPointerSelect(int screenX){
        if(dragStart.get() == null){
            // If there's a selection, clear it
            clearSelection();
        }else{
            dragStart.set(null);
            positionCursor(screenX);
        }
    }

    public String copy(){
        return copy(false);
    }

    public String copy(boolean delete){
        var selection = label.selection().get();
        if(selection != null){
            var s = editor().get().substring(selection.start(), selection.end());
            if(delete){
                editor().get().delete(selection.start(), selection.end());
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
            editor.ifSet(e -> e.delete(selection.start(), selection.end()));
            clearSelection();
            moveCursor(selection.start());
        }else{
            deletePrevious();
        }
    }

    public void deletePrevious(){
        if(cursorPos.get() > 0){
            editor.ifSet(e -> e.delete(cursorPos.get() - 1, cursorPos.get()));
            cursorBackward();
        }
    }

    public void deleteCurrent(){
        editor.ifSet(e -> {
            if(cursorPos.get() < e.length()){
                e.delete(cursorPos.get(), cursorPos.get() + 1);
            }
        });
    }

    public void setText(String s){
        selectAll();
        delete();
        insert(s);
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
        if(nextCursor < 0 || nextCursor > editor().get().length()){
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
        pos = Math.min(pos, editor().get().length());
        cursorPos.set(pos);
    }

    public String getValue(){
        return editor.get().toString();
    }

    protected void positionCursor(int screenX){
        moveCursor(calculateCursorIndex(label.localizeX(screenX)));
    }

    protected int calculateCursorIndex(float xCoord){
        return label.getRuler().getCharIndex(editor().get().toString(), xCoord);
    }

}
