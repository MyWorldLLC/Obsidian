package myworld.obsidian.display;

import io.github.humbleui.skija.*;
import io.github.humbleui.types.Rect;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.StyleRules;
import myworld.obsidian.display.skin.Variables;
import myworld.obsidian.geometry.Bounds2D;
import myworld.obsidian.geometry.Rectangle;
import myworld.obsidian.geometry.SvgPath;

public class Renderer {

    public static void render(Canvas canvas, Bounds2D componentBounds, StyleClass style, Variables renderVars){

            var boundingRect = new Rect(componentBounds.left(), componentBounds.top(), componentBounds.right(), componentBounds.bottom());

            Paint fill = null;
            if(style.hasRule(StyleRules.COLOR)){
                fill = new Paint();
                fill.setColor(style.rule(StyleRules.COLOR, Colors.WHITE).toARGB());
            }

            Paint stroke = null;
            if(style.hasAny(StyleRules.BORDER_CAP, StyleRules.BORDER_COLOR, StyleRules.BORDER_JOIN,
                    StyleRules.BORDER_MITER, StyleRules.BORDER_WIDTH)){
                stroke = new Paint()
                        .setStroke(true)
                        .setColor(style.rule(StyleRules.BORDER_COLOR, Colors.TRANSPARENT).toARGB())
                        .setStrokeWidth(style.rule(StyleRules.BORDER_WIDTH, 1f))
                        .setStrokeCap(skiaCap(style.rule(StyleRules.BORDER_CAP, Borders.CAP_SQUARE)))
                        .setStrokeJoin(skiaJoin(style.rule(StyleRules.BORDER_JOIN, Borders.JOIN_BEVEL)))
                        .setStrokeMiter(style.rule(StyleRules.BORDER_MITER, 0f));
            }

            Path renderPath = new Path();

            renderPath.addPath(createSkiaGeometry(boundingRect, style, renderVars));

            var visualBounds = renderPath.getBounds();
            // TODO - move this to rendering - some content should be scaled, some should be clipped
            if(visualBounds.getWidth() > componentBounds.width() || visualBounds.getHeight() > componentBounds.height()){
                // Scale content to fit its laid-out componentBounds rather than clipping it off
                renderPath.transform(Matrix33.makeTranslate(-componentBounds.left(), -componentBounds.top()))
                        .transform(Matrix33.makeScale(
                                componentBounds.width()/visualBounds.getWidth(),
                                componentBounds.height()/visualBounds.getHeight()))
                        .transform(Matrix33.makeTranslate(componentBounds.left(), componentBounds.top()));
            }

            if(fill != null){
                canvas.drawPath(renderPath, fill);
            }

            if(stroke != null){
                canvas.drawPath(renderPath, stroke);
            }
    }

    public static Path createSkiaGeometry(Rect boundingRect, StyleClass style, Variables renderVars){
        Object geometry = style.rule(StyleRules.GEOMETRY);
        var path = new Path();
        if(geometry instanceof Rectangle r){
           path.addRect(new Rect(boundingRect.getLeft(), (float)(boundingRect.getTop() + r.dimensions().height()), (float)(boundingRect.getLeft() + r.dimensions().width()), boundingRect.getBottom()));
        }else if(geometry instanceof SvgPath svg){
            var svgPath = Path.makeFromSVGString(svg.path())
                    .transform(Matrix33.makeTranslate(boundingRect.getLeft(), boundingRect.getTop()));
            if(svgPath.isValid()) {
                path.addPath(svgPath);
            }else{
                path.addRect(boundingRect);
            }
        }else if(geometry instanceof String varName){
            String text = renderVars.get(varName, String.class);
            // TODO - text rendering
        }else{
            // Default to filling in the componentBounds as a rectangle
            path.addRect(boundingRect);
        }
        return path;
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
