package myworld.obsidian.display;

import io.github.humbleui.skija.*;
import io.github.humbleui.skija.paragraph.*;
import io.github.humbleui.types.Point;
import io.github.humbleui.types.Rect;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.StyleRules;
import myworld.obsidian.display.skin.Variables;
import myworld.obsidian.geometry.*;
import myworld.obsidian.text.Text;
import myworld.obsidian.text.TextDecoration;
import myworld.obsidian.text.TextShadow;

import java.util.List;

public class Renderer implements AutoCloseable{

    protected final FontCollection fontCollection;
    protected final TypefaceFontProvider typeProvider;

    public Renderer(){
        fontCollection = new FontCollection();
        fontCollection.setDefaultFontManager(FontMgr.getDefault());

        typeProvider = new TypefaceFontProvider();
        fontCollection.setAssetFontManager(typeProvider);
        fontCollection.setEnableFallback(true);
    }

    public void registerFont(Typeface typeface){
        typeProvider.registerTypeface(typeface);
    }

    public void render(Canvas canvas, Bounds2D componentBounds, StyleClass style, Variables renderVars){

        var boundingRect = new Rect(componentBounds.left(), componentBounds.top(), componentBounds.right(), componentBounds.bottom());

        var geometry = (Object) createSkiaGeometry(boundingRect, style, renderVars);

        Paint fill = getFill(style);
        Paint stroke = getStroke(style);

        Path renderPath = new Path();

        if(geometry instanceof Path p){
            renderPath.addPath(p);
        }
        Move position = style.rule(StyleRules.POSITION);
        Rotate rotation = style.rule(StyleRules.ROTATION);

        Matrix33 transform = Matrix33.IDENTITY;
        if(rotation != null) {
            transform = Matrix33.makeRotate(-rotation.angle(),
                            new Point(componentBounds.left() + componentBounds.width() / 2f,
                                    componentBounds.top() + componentBounds.height() / 2f))
                    .makeConcat(transform);
        }

        if(position != null){
            transform = Matrix33.makeTranslate(
                            toPixels(position.x(), componentBounds.width()),
                            toPixels(position.y(), componentBounds.height()))
                    .makeConcat(transform);
        }

        renderPath.transform(transform);

        var visualBounds = renderPath.getBounds();
        if (visualBounds.getWidth() > boundingRect.getWidth() || visualBounds.getHeight() > boundingRect.getHeight()) {

            switch (style.rule(StyleRules.OVERFLOW_MODE, OverflowModes.SCALE)){
                case OverflowModes.CLIP ->
                    canvas.clipRect(boundingRect, true);
                case OverflowModes.SCALE_UNIFORM ->
                        /*{
                            final var factor = Math.min(
                                    boundingRect.getWidth() / visualBounds.getWidth(),
                                    boundingRect.getHeight() / visualBounds.getHeight()
                            );
                            canvas.scale(factor, factor);
                        }*/
                    renderPath.transform(Matrix33.makeTranslate(-boundingRect.getLeft(), -boundingRect.getTop()))
                            .transform(Matrix33.makeScale(
                                    Math.min(
                                            boundingRect.getWidth() / visualBounds.getWidth(),
                                            boundingRect.getHeight() / visualBounds.getHeight()
                                    )))
                            .transform(Matrix33.makeTranslate(boundingRect.getLeft(), boundingRect.getTop()));
                case OverflowModes.SCALE ->
                        //canvas.scale(boundingRect.getWidth() / visualBounds.getWidth(), boundingRect.getHeight() / visualBounds.getHeight());
                    renderPath.transform(Matrix33.makeTranslate(-boundingRect.getLeft(), -boundingRect.getTop()))
                            .transform(Matrix33.makeScale(
                                    boundingRect.getWidth() / visualBounds.getWidth(),
                                    boundingRect.getHeight() / visualBounds.getHeight()))
                            .transform(Matrix33.makeTranslate(boundingRect.getLeft(), boundingRect.getTop()));
            }

        }

        if(geometry instanceof Paragraph p){
            canvas.setMatrix(transform);
            p.layout(boundingRect.getWidth());
            p.paint(canvas, boundingRect.getLeft(), boundingRect.getTop());
        }

        if (fill != null) {
            canvas.drawPath(renderPath, fill);
        }

        if (stroke != null) {
            canvas.drawPath(renderPath, stroke);
        }

        canvas.resetMatrix();
    }

    public Object createSkiaGeometry(Rect boundingRect, StyleClass style, Variables renderVars){
        Object geometry = style.rule(StyleRules.GEOMETRY);
        var path = new Path();
        if(geometry instanceof Rectangle r){
           path.addRect(new Rect(boundingRect.getLeft(), boundingRect.getTop() + r.height(), boundingRect.getLeft() + r.width(), boundingRect.getBottom()));
        }else if(geometry instanceof SvgPath svg){
            var svgPath = Path.makeFromSVGString(svg.path())
                    .transform(Matrix33.makeTranslate(boundingRect.getLeft(), boundingRect.getTop()));
            if(svgPath.isValid()) {
                path.addPath(svgPath);
            }else{
                path.addRect(boundingRect);
            }
        }else if(geometry instanceof String varName){
            Text text = renderVars.get(varName, Text.class);
            try (TextStyle ts = new TextStyle();
                 ParagraphStyle ps = new ParagraphStyle();
                 ParagraphBuilder pb = new ParagraphBuilder(ps, fontCollection)) {

                // TODO - different styling for different spans
                ts.setColor(style.rule(StyleRules.COLOR, Colors.BLACK).toARGB());
                ts.setFontFamily(style.rule(StyleRules.FONT_FAMILY));
                ts.setFontSize(style.rule(StyleRules.FONT_SIZE, 12));
                ts.setFontStyle(getFontStyle(style));
                ts.setDecorationStyle(getTextDecoration(style));

                var backgroundPaint = new Paint();
                backgroundPaint.setColor(style.rule(StyleRules.TEXT_BACKGROUND_COLOR, Colors.TRANSPARENT).toARGB());
                ts.setBackground(backgroundPaint);

                applyShadows(ts, style, boundingRect);

                pb.pushStyle(ts);
                pb.addText(text.text());

                return pb.build();
            }

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

    protected static float toPixels(Distance distance, float size){
        return switch (distance.unit()){
            case PIXELS -> distance.asInt();
            case PERCENTAGE -> distance.asFloat() / 100f * size;
        };
    }

    protected static Paint getFill(StyleClass style){
        Paint fill = null;
        if (style.hasRule(StyleRules.COLOR)) {
            fill = new Paint();
            fill.setColor(style.rule(StyleRules.COLOR, Colors.WHITE).toARGB());
        }
        return fill;
    }

    protected static Paint getStroke(StyleClass style){
        Paint stroke = null;
        if (style.hasAny(StyleRules.BORDER_CAP, StyleRules.BORDER_COLOR, StyleRules.BORDER_JOIN,
                StyleRules.BORDER_MITER, StyleRules.BORDER_WIDTH)) {
            stroke = new Paint()
                    .setStroke(true)
                    .setColor(style.rule(StyleRules.BORDER_COLOR, Colors.TRANSPARENT).toARGB())
                    .setStrokeWidth(style.rule(StyleRules.BORDER_WIDTH, 1f))
                    .setStrokeCap(skiaCap(style.rule(StyleRules.BORDER_CAP, Borders.CAP_SQUARE)))
                    .setStrokeJoin(skiaJoin(style.rule(StyleRules.BORDER_JOIN, Borders.JOIN_BEVEL)))
                    .setStrokeMiter(style.rule(StyleRules.BORDER_MITER, 0f));
        }
        return stroke;
    }

    protected static FontStyle getFontStyle(StyleClass style) {
        return switch (style.rule(StyleRules.FONT_STYLE, myworld.obsidian.text.TextStyle.NORMAL)) {
            case NORMAL -> FontStyle.NORMAL;
            case BOLD -> FontStyle.BOLD;
            case ITALIC -> FontStyle.ITALIC;
            case BOLD_ITALIC -> FontStyle.BOLD_ITALIC;
        };
    }

    protected static DecorationStyle getTextDecoration(StyleClass style){
        if(style.hasRule(StyleRules.TEXT_DECORATION)){
            TextDecoration textStyle = style.rule(StyleRules.TEXT_DECORATION);
            return new DecorationStyle(
                    textStyle.underline(),
                    false,
                    textStyle.strikethrough(),
                    false,
                    textStyle.color() != null ? textStyle.color().toARGB() : style.rule(StyleRules.COLOR, Colors.BLACK).toARGB(),
                    switch (textStyle.style()){
                        case SOLID -> DecorationLineStyle.SOLID;
                        case DASHED -> DecorationLineStyle.DASHED;
                        case DOTTED -> DecorationLineStyle.DOTTED;
                        case DOUBLE -> DecorationLineStyle.DOUBLE;
                        case WAVY -> DecorationLineStyle.WAVY;
                    },
                    textStyle.thickness()
            );
        }
        return DecorationStyle.NONE;
    }

    protected static void applyShadows(TextStyle ts, StyleClass style, Rect boundingRect){
        if(style.hasRule(StyleRules.TEXT_SHADOW)){
            var shadows = style.rule(StyleRules.TEXT_SHADOW);
            if(shadows instanceof List shadowList){
                for(var textShadow : shadowList){
                    ts.addShadow(getTextShadow((TextShadow) textShadow, boundingRect));
                }
            }else if(shadows instanceof TextShadow shadow){
                ts.addShadow(getTextShadow(shadow, boundingRect));
            }
        }
    }

    protected static Shadow getTextShadow(TextShadow shadow, Rect boundingRect){
        return new Shadow(
                shadow.color().toARGB(),
                toPixels(shadow.offset().x(), boundingRect.getWidth()),
                toPixels(shadow.offset().y(), boundingRect.getHeight()),
                shadow.blurSigma());
    }

    @Override
    public void close() {
        fontCollection.close();
        typeProvider.close();
    }
}
