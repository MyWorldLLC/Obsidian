package myworld.obsidian.text;

import myworld.obsidian.display.ColorRGBA;

public record TextDecoration(boolean underline, boolean strikethrough, ColorRGBA color, LineStyle style, float thickness) {

    public static final float DEFAULT_THICKNESS = 1.0f;

    public enum LineStyle {
        SOLID,
        DASHED,
        DOTTED,
        DOUBLE,
        WAVY
    }

    public static TextDecoration strikeThrough(ColorRGBA color, LineStyle lineStyle, float thickness){
        return new TextDecoration(false, true, color, lineStyle, thickness);
    }

    public static TextDecoration strikeThrough(ColorRGBA color, float thickness){
        return new TextDecoration(false, true, color, LineStyle.SOLID, thickness);
    }

    public static TextDecoration underline(ColorRGBA color, LineStyle lineStyle, float thickness){
        return new TextDecoration(true, false, color, lineStyle, thickness);
    }

    public static TextDecoration underline(ColorRGBA color, float thickness){
        return new TextDecoration(true, false, color, LineStyle.SOLID, thickness);
    }
}
