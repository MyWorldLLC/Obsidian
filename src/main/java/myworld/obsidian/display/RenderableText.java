package myworld.obsidian.display;

import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.TextBlob;
import io.github.humbleui.types.Rect;
import myworld.obsidian.text.TextDecoration;
import myworld.obsidian.text.TextShadow;

public record RenderableText(TextBlob text, Font font, Rect bounds, TextDecoration decorations, TextShadow[] shadows) {}
