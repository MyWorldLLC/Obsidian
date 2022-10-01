package myworld.obsidian.text;

import myworld.obsidian.display.ColorRGBA;

public record TextDecoration(boolean underline, boolean strikethrough, ColorRGBA color, LineStyle style, float thickness) {
    public enum LineStyle {
        SOLID,
        DASHED,
        DOTTED,
        DOUBLE,
        WAVY
    }

    public TextDecoration strikeThrough(ColorRGBA color, float thickness){
        return new TextDecoration(false, true, color, LineStyle.SOLID, thickness);
    }

    public TextDecoration underline(ColorRGBA color, float thickness){
        return new TextDecoration(true, false, color, LineStyle.SOLID, thickness);
    }
}
