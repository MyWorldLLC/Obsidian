package myworld.client.obsidian;

import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.cursor.CursorType;

public class ObsidianCursor {

    public enum Type {
        SIMPLE, IMAGE
    }

    protected final Type type;
    protected final CursorType fxCursorType;
    protected final ObsidianPixels cursorPixels;
    protected final double hotspotX;
    protected final double hotspotY;

    private ObsidianCursor(Type type, CursorType fxCursorType, ObsidianPixels cursorPixels, double hotspotX, double hotspotY){
        this.type = type;
        this.fxCursorType = fxCursorType;
        this.cursorPixels = cursorPixels;
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;
    }

    public static ObsidianCursor ofType(CursorType fxCursorType){
        return new ObsidianCursor(Type.SIMPLE, fxCursorType, null, 0, 0);
    }

    public static ObsidianCursor ofImage(ObsidianPixels pixels, double hotspotX, double hotspotY){
        return new ObsidianCursor(Type.IMAGE, null, pixels, hotspotX, hotspotY);
    }

    public Type getType() {
        return type;
    }

    public CursorType getFxCursorType() {
        return fxCursorType;
    }

    public ObsidianPixels getCursorPixels() {
        return cursorPixels;
    }

    public double getHotspotX() {
        return hotspotX;
    }

    public double getHotspotY() {
        return hotspotY;
    }
}
