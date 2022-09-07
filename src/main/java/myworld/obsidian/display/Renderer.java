package myworld.obsidian.display;

import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.StyleRules;
import myworld.obsidian.geometry.Bounds2D;
import myworld.obsidian.geometry.Rectangle;
import myworld.obsidian.geometry.SvgPath;

public class Renderer {

    public static void render(Canvas canvas, Bounds2D componentBounds, StyleClass style){

            var boundingRect = new Rect(componentBounds.left(), componentBounds.top(), componentBounds.right(), componentBounds.bottom());

            var fill = new Paint();
            fill.setColor(style.rule(StyleRules.COLOR, Colors.WHITE).toARGB());

            Paint stroke = null;
            if(style.hasAny(StyleRules.BORDER_CAP, StyleRules.BORDER_COLOR, StyleRules.BORDER_JOIN,
                    StyleRules.BORDER_MITER, StyleRules.BORDER_WIDTH)){
                stroke = new Paint()
                        .setStroke(true)
                        .setColor(style.rule(StyleRules.BORDER_COLOR, Colors.TRANSPARENT).toARGB())
                        .setStrokeWidth(style.rule(StyleRules.BORDER_WIDTH, 0f))
                        .setStrokeCap(skiaCap(style.rule(StyleRules.BORDER_CAP, Borders.CAP_SQUARE)))
                        .setStrokeJoin(skiaJoin(style.rule(StyleRules.BORDER_JOIN, Borders.JOIN_BEVEL)))
                        .setStrokeMiter(style.rule(StyleRules.BORDER_MITER, 0f));
            }

            Path renderPath = new Path();

            Object geometry = style.rule(StyleRules.GEOMETRY);
            if(geometry instanceof Rectangle r){
                renderPath.addRect(new Rect(componentBounds.left(), (float)(componentBounds.top() + r.dimensions().height()), (float)(componentBounds.left() + r.dimensions().width()), componentBounds.bottom()));
            }else if(geometry instanceof SvgPath svg){
                var path = Path.makeFromSVGString(svg.path())
                        .transform(Matrix33.makeTranslate(componentBounds.left(), componentBounds.top()));
                if(path.isValid()) {
                    renderPath.addPath(path);
                }else{
                    renderPath.addRect(boundingRect);
                }
            }else{
                // Default to filling in the componentBounds as a rectangle
                renderPath.addRect(boundingRect);
            }
            var visualBounds = renderPath.getBounds();
            if(visualBounds.getWidth() > componentBounds.width() || visualBounds.getHeight() > componentBounds.height()){
                // Scale content to fit its laid-out componentBounds rather than clipping it off
                renderPath.transform(Matrix33.makeTranslate(-componentBounds.left(), -componentBounds.top()))
                        .transform(Matrix33.makeScale(
                                componentBounds.width()/visualBounds.getWidth(),
                                componentBounds.height()/visualBounds.getHeight()))
                        .transform(Matrix33.makeTranslate(componentBounds.left(), componentBounds.top()));
            }

            canvas.drawPath(renderPath, fill);
            if(stroke != null){
                canvas.drawPath(renderPath, stroke);
            }
    }

    public static PaintStrokeCap skiaCap(String cap){
        return switch (cap){
            case Borders.CAP_BUTT -> PaintStrokeCap.BUTT;
            case Borders.CAP_ROUND -> PaintStrokeCap.ROUND;
            case Borders.CAP_SQUARE -> PaintStrokeCap.SQUARE;
            default -> PaintStrokeCap.SQUARE;
        };
    }

    public static PaintStrokeJoin skiaJoin(String join){
        return switch (join){
            case Borders.JOIN_BEVEL -> PaintStrokeJoin.BEVEL;
            case Borders.JOIN_ROUND -> PaintStrokeJoin.ROUND;
            case Borders.JOIN_MITER -> PaintStrokeJoin.MITER;
            default -> PaintStrokeJoin.BEVEL;
        };
    }

}
