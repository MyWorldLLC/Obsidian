package myworld.obsidian.display;

import io.github.humbleui.skija.Font;

import java.util.Arrays;

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

    public float getWidth(String s){
        var widths = getWidths(s);
        float width = 0;
        for(int i = 0; i < widths.length; i++){
            width += widths[i];
        }
        return width;
    }

    public float getLineHeight(){
        return font.getMetrics().getHeight();
    }

    public int getCharIndex(String s, float xCoord){
        var widths = getWidths(s);
        var positions = getXPositions(s);

        if(xCoord < 0){
            return 0;
        }

        for(int i = 0; i < s.length(); i++){
            float pos = positions[i];
            float advance = widths[i];
            if(pos <= xCoord && xCoord <= pos + advance){
                // We're in this character's bounds - if we're in
                // the left half, return this character, otherwise
                // return the next character.
                return xCoord <= (pos + 0.5f * advance) ? i : i + 1;
            }
        }
        return s.length();
    }

}
