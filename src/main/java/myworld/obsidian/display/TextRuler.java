package myworld.obsidian.display;

import io.github.humbleui.skija.Font;
import io.github.humbleui.types.Rect;

/**
 * This is safe to use within a single render pass, but should never be retained between
 * render passes as the renderer/typefaces may be cleaned between render passes (such as
 * when the display is resized).
 */
public class TextRuler {

    protected final Renderer renderer;
    protected final Font font;

    protected TextRuler(Renderer renderer, Font font){
        this.renderer = renderer;
        this.font = font;
    }

    public float[] getXPositions(String s){
        var glyphs = font.getStringGlyphs(s);
        return font.getXPositions(glyphs);
    }

    public float[] getXPositions(String s, float offset){
        var glyphs = font.getStringGlyphs(s);
        return font.getXPositions(glyphs, offset);
    }

    public float[] getWidths(String s){
        var glyphs = font.getStringGlyphs(s);
        return font.getWidths(glyphs);
    }

    public float getLineHeight(){
        return font.getMetrics().getHeight();
    }

}
