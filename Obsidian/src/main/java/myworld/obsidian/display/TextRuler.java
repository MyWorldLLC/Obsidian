package myworld.obsidian.display;

public abstract class TextRuler {

    public abstract float[] getXPositions(String s);

    public abstract float[] getXPositions(String s, float offset);

    public abstract float[] getWidths(String s);

    public abstract float getWidth(String s);

    public abstract float getLineHeight();

    public abstract int getCharIndex(String s, float xCoord);
}
