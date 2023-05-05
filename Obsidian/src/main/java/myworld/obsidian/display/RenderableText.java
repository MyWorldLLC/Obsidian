package myworld.obsidian.display;

import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.TextLine;
import io.github.humbleui.types.Rect;
import myworld.obsidian.text.TextDecoration;
import myworld.obsidian.text.TextShadow;

public record RenderableText(String text, TextLine line, Font font, Rect bounds, ColorRGBA color, ColorRGBA backgroundColor, TextDecoration decorations, TextShadow[] shadows) {}
