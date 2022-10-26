package myworld.obsidian.display;

import io.github.humbleui.skija.*;
import io.github.humbleui.skija.paragraph.*;
import io.github.humbleui.skija.shaper.Shaper;
import io.github.humbleui.skija.svg.SVGDOM;
import io.github.humbleui.types.Point;
import io.github.humbleui.types.Rect;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.StyleLookup;
import myworld.obsidian.display.skin.StyleRules;
import myworld.obsidian.display.skin.Variables;
import myworld.obsidian.geometry.*;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.text.Text;
import myworld.obsidian.text.TextDecoration;
import myworld.obsidian.text.TextShadow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Renderer implements AutoCloseable {

    protected final FontCollection fontCollection;
    protected final TypefaceFontProvider typeProvider;
    protected final Shaper shaper;

    protected final Map<ResourceHandle, SVGDOM> svgs;
    protected final Map<ResourceHandle, Image> images;

    protected final ValueProperty<ColorRGBA> debugColor;

    public Renderer(){
        fontCollection = new FontCollection();
        fontCollection.setDefaultFontManager(FontMgr.getDefault());

        typeProvider = new TypefaceFontProvider();
        fontCollection.setAssetFontManager(typeProvider);
        fontCollection.setEnableFallback(true);

        shaper = Shaper.make(FontMgr.getDefault());

        debugColor = new ValueProperty<>();

        svgs = new HashMap<>();
        images = new HashMap<>();
    }

    public ValueProperty<ColorRGBA> debugBoundsColor(){
        return debugColor;
    }

    public void registerFont(Typeface typeface){
        typeProvider.registerTypeface(typeface);
    }

    public void registerImage(ResourceHandle handle, Image image){
        images.put(handle, image);
    }

    public void registerSvg(ResourceHandle handle, SVGDOM svg){
        svgs.put(handle, svg);
    }

    public void render(Canvas canvas, Bounds2D componentBounds, StyleClass style, Variables renderVars, StyleLookup styles){
        canvas.save();

        var boundingRect = new Rect(componentBounds.left() - 0.5f, componentBounds.top() - 0.5f, componentBounds.right() - 0.5f, componentBounds.bottom() - 0.5f);
        if(debugColor.get() != null){
            var debugPaint = new Paint();
            debugPaint.setColor(debugColor.get().toARGB());
            debugPaint.setStroke(true);
            debugPaint.setStrokeWidth(1f);
            canvas.drawRect(boundingRect, debugPaint);
        }

        var geometry = (Object) createSkiaGeometry(boundingRect, style, renderVars, styles);

        Paint fill = getFill(style, renderVars);
        Paint stroke = getStroke(style, renderVars);

        Move position = style.rule(StyleRules.POSITION, renderVars);
        Rotate rotation = style.rule(StyleRules.ROTATION, renderVars);

        Matrix33 transform = Matrix33.IDENTITY;
        if(rotation != null) {
            transform = Matrix33.makeRotate(-rotation.angle(),
                            new Point(componentBounds.left() + componentBounds.width() / 2f,
                                    componentBounds.top() + componentBounds.height() / 2f))
                    .makeConcat(transform);
        }

        if(position != null){
            transform = Matrix33.makeTranslate(
                            // The shift by 0.5f aligns pixels so that they make a crisp line rather than a blurred one
                            toPixels(position.x(), componentBounds.width()) - 0.5f,
                            toPixels(position.y(), componentBounds.height()) - 0.5f)
                    .makeConcat(transform);
        }

        if(geometry instanceof Path p){
            Path renderPath = new Path();

            renderPath.addPath(p);

            renderPath.transform(transform);

            var visualBounds = renderPath.getBounds();
            if (visualBounds.getWidth() > boundingRect.getWidth() || visualBounds.getHeight() > boundingRect.getHeight()) {

                switch (style.rule(StyleRules.OVERFLOW_MODE, renderVars, OverflowModes.SCALE)){
                    case OverflowModes.CLIP ->
                            canvas.clipRect(boundingRect, true);
                    case OverflowModes.SCALE_UNIFORM ->
                            transform = Matrix33.IDENTITY
                                    .makeConcat(Matrix33.makeTranslate(visualBounds.getLeft(), visualBounds.getTop()))
                                    .makeConcat(Matrix33.makeScale(
                                            Math.min(
                                                    boundingRect.getWidth() / visualBounds.getWidth(),
                                                    boundingRect.getHeight() / visualBounds.getHeight()
                                            )))
                                    .makeConcat(Matrix33.makeTranslate(-visualBounds.getLeft(), -visualBounds.getTop()));
                    case OverflowModes.SCALE ->
                            transform = Matrix33.IDENTITY
                                    .makeConcat(Matrix33.makeTranslate(visualBounds.getLeft(), visualBounds.getTop()))
                                    .makeConcat(Matrix33.makeScale(
                                            boundingRect.getWidth() / visualBounds.getWidth(),
                                            boundingRect.getHeight() / visualBounds.getHeight()))
                                    .makeConcat(Matrix33.makeTranslate(-visualBounds.getLeft(), -visualBounds.getTop()));
                }

                renderPath.transform(transform);
            }

            if (fill != null) {
                canvas.drawPath(renderPath, fill);
            }

            if (stroke != null) {
                canvas.drawPath(renderPath, stroke);
            }

        }if(geometry instanceof RenderableText text){
            // Note: Because text can specify its own style class,
            // conversion of the high-level Text object to RenderableText
            // performs style class merging. This means that all styleable
            // properties used here must be assigned to the RenderableText
            // during creation, and none of the style variables (such as fill color)
            // available from the outer scopes here may be used.
            canvas.setMatrix(transform);
            canvas.clipRect(boundingRect, true);

            var textColor = new Paint();
            textColor.setColor(text.color().toARGB());

            if(text.backgroundColor() != null){
                var backgroundPaint = new Paint();
                backgroundPaint.setColor(text.backgroundColor().toARGB());
                var background = new Rect(
                        boundingRect.getLeft(),
                        boundingRect.getTop(),
                        boundingRect.getLeft() + text.text().getBlockBounds().getWidth(),
                        boundingRect.getTop() + text.text().getBlockBounds().getHeight()
                );

                canvas.drawRect(background, backgroundPaint);
            }

            if(text.shadows() != null){
                var shadowPaint = new Paint();
                for(var shadow : text.shadows()){
                    shadowPaint.setColor(shadow.color().toARGB());
                    shadowPaint.setMaskFilter(MaskFilter.makeBlur(FilterBlurMode.NORMAL, shadow.blurSigma(), false));
                    renderDecoratedText(canvas, text.text(), text.decorations(),
                            toPixels(shadow.offset().x(), boundingRect.getWidth()) + boundingRect.getLeft(),
                            toPixels(shadow.offset().y(), boundingRect.getHeight()) + boundingRect.getTop(),
                            shadowPaint);
                }
            }

            renderDecoratedText(canvas, text.text(), text.decorations(), text.bounds().getLeft(), text.bounds().getTop(), textColor);
        }

        canvas.restore();
    }

    protected void renderDecoratedText(Canvas canvas, TextBlob text, TextDecoration decor, float x, float y, Paint fill){
        canvas.drawTextBlob(text, x, y, fill);

        if(decor != null){
            if(decor.underline()){
                float underlineY = Math.round(y + text.getTightBounds().getBottom()) - 0.5f;
                canvas.drawLine(x, underlineY, x + text.getBlockBounds().getWidth(), underlineY, fill);
            }
            if(decor.strikethrough()){
                float strikeY = Math.round(y + (text.getTightBounds().getTop() + text.getTightBounds().getBottom()) / 2.0f) - 0.5f;
                canvas.drawLine(x, strikeY, x + text.getBlockBounds().getWidth(), strikeY, fill);
            }
        }


    }

    public Object createSkiaGeometry(Rect boundingRect, StyleClass style, Variables renderVars, StyleLookup styles){
        Object geometry = style.rule(StyleRules.GEOMETRY, renderVars);
        var path = new Path();
        if(geometry instanceof Rectangle r){
           path.addRect(
                   new Rect(
                           boundingRect.getLeft(),
                           boundingRect.getTop(),
                           boundingRect.getLeft() + pixelWidth(r.width(), boundingRect),
                           boundingRect.getTop() + pixelHeight(r.height(), boundingRect)
                   ));
        }else if(geometry instanceof Ellipse ellipse){
            path.addOval(
                    new Rect(
                            boundingRect.getLeft(),
                            boundingRect.getTop(),
                            boundingRect.getLeft() + pixelWidth(ellipse.width(), boundingRect),
                            boundingRect.getTop() + pixelHeight(ellipse.height(), boundingRect)
                    ));
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

            if(text.hasStyle()){
                style = StyleClass.merge(text.style(), style);
            }

            var font = getFont(style);

            TextBlob blob = shaper.shape(text.text(), font, boundingRect.getWidth());

            var color = style.rule(StyleRules.COLOR, renderVars, Colors.BLACK);
            ColorRGBA backgroundColor = null;
            if(style.hasRule(StyleRules.TEXT_BACKGROUND_COLOR)){
                backgroundColor = style.rule(StyleRules.TEXT_BACKGROUND_COLOR, renderVars);
            }

            return new RenderableText(blob, font, boundingRect, color, backgroundColor, getTextDecoration(style, renderVars), getShadows(style, renderVars));

        }else{
            // Default to filling in the componentBounds as a rectangle
            path.addRect(boundingRect);
        }

        return path;
    }

    public Font getFont(StyleClass style){
        Typeface typeface;
        if(style != null){
            Typeface[] typefaces = fontCollection.findTypefaces(new String[]{style.rule(StyleRules.FONT_FAMILY, Variables.empty())}, getFontStyle(style, Variables.empty()));
            typeface = typefaces != null && typefaces.length > 0 ? typefaces[0] : fontCollection.defaultFallback();
        }else{
            typeface = fontCollection.defaultFallback();
        }

        var fontSize = 12;
        if(style != null){
            fontSize = style.rule(StyleRules.FONT_SIZE, Variables.empty(),12);
        }

        var font = new Font(typeface);
        font.setHinting(FontHinting.NORMAL);
        font.setSize(fontSize);
        font.setSubpixel(true);
        return font;
    }

    public Rect measureText(Text t){
        return getFont(t.style()).measureText(t.text());
    }

    public float measureTextWidth(Text t){
        return getFont(t.style()).measureText(t.text()).getWidth();
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

    protected static float pixelWidth(Distance distance, Rect bounds){
        return toPixels(distance, bounds.getWidth());
    }

    protected static float pixelHeight(Distance distance, Rect bounds){
        return toPixels(distance, bounds.getHeight());
    }

    protected static float toPixels(Distance distance, float size){
        return switch (distance.unit()){
            case PIXELS -> distance.asInt();
            case PERCENTAGE -> distance.asFloat() / 100f * size;
        };
    }

    protected static Paint getFill(StyleClass style, Variables renderVars){
        Paint fill = null;
        if (style.hasRule(StyleRules.COLOR)) {
            fill = new Paint();
            fill.setColor(style.rule(StyleRules.COLOR, renderVars, Colors.WHITE).toARGB());
        }
        return fill;
    }

    protected static Paint getStroke(StyleClass style, Variables renderVars){
        Paint stroke = null;
        if (style.hasAny(StyleRules.BORDER_CAP, StyleRules.BORDER_COLOR, StyleRules.BORDER_JOIN,
                StyleRules.BORDER_MITER, StyleRules.BORDER_WIDTH)) {
            stroke = new Paint()
                    .setStroke(true)
                    .setColor(style.rule(StyleRules.BORDER_COLOR, renderVars, Colors.TRANSPARENT).toARGB())
                    .setStrokeWidth(style.rule(StyleRules.BORDER_WIDTH, renderVars, 1f))
                    .setStrokeCap(skiaCap(style.rule(StyleRules.BORDER_CAP, renderVars, Borders.CAP_SQUARE)))
                    .setStrokeJoin(skiaJoin(style.rule(StyleRules.BORDER_JOIN, renderVars, Borders.JOIN_BEVEL)))
                    .setStrokeMiter(style.rule(StyleRules.BORDER_MITER, renderVars, 0f));
        }
        return stroke;
    }

    protected static FontStyle getFontStyle(StyleClass style, Variables renderVars) {
        return switch (style.rule(StyleRules.FONT_STYLE, renderVars, myworld.obsidian.text.TextStyle.NORMAL)) {
            case NORMAL -> FontStyle.NORMAL;
            case BOLD -> FontStyle.BOLD;
            case ITALIC -> FontStyle.ITALIC;
            case BOLD_ITALIC -> FontStyle.BOLD_ITALIC;
        };
    }

    protected static TextDecoration getTextDecoration(StyleClass style, Variables renderVars){
        return style.rule(StyleRules.TEXT_DECORATION, renderVars);
    }

    @SuppressWarnings("unchecked")
    protected static TextShadow[] getShadows(StyleClass style, Variables renderVars){
        if(style.hasRule(StyleRules.TEXT_SHADOW)){
            var shadows = style.rule(StyleRules.TEXT_SHADOW, renderVars);
            if(shadows instanceof List shadowList){
                return ((List<TextShadow>) shadowList).toArray(new TextShadow[]{});
            }else if(shadows instanceof TextShadow shadow){
                return new TextShadow[]{shadow};
            }
        }
        return null;
    }

    @Override
    public void close() {
        fontCollection.close();
        typeProvider.close();
        shaper.close();

        images.forEach((h, i) -> i.close());
        svgs.forEach((h, s) -> s.close());
    }
}
