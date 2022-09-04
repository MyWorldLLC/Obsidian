package myworld.obsidian.display;

import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Region;
import io.github.humbleui.types.IRect;
import io.github.humbleui.types.Rect;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.StyleRules;
import myworld.obsidian.geometry.Bounds2D;
import io.github.humbleui.skija.Canvas;

public class Renderer {

    public static void render(Canvas canvas, Bounds2D bounds, StyleClass style){

        var clip = new Region();
        clip.setRect(new IRect((int)bounds.left(), (int)bounds.top(), (int)bounds.right(), (int)bounds.bottom()));
        canvas.clipRegion(clip);


        System.out.println((Object)style.rule(StyleRules.COLOR));
        var paint = new Paint();
        paint.setColor(style.rule(StyleRules.COLOR, Colors.WHITE).toInt());

        canvas.drawRect(new Rect((float)bounds.left(), (float)bounds.top(), (float)bounds.right(), (float)bounds.bottom()), paint);
    }

}
