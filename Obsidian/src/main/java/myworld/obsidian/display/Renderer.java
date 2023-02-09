package myworld.obsidian.display;

import io.github.humbleui.skija.*;
import io.github.humbleui.skija.paragraph.*;
import io.github.humbleui.skija.shaper.Shaper;
import io.github.humbleui.skija.svg.SVGLengthContext;
import io.github.humbleui.skija.svg.SVGLengthType;
import io.github.humbleui.types.Point;
import io.github.humbleui.types.RRect;
import io.github.humbleui.types.Rect;
import myworld.obsidian.display.skin.*;
import myworld.obsidian.geometry.*;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.text.Text;
import myworld.obsidian.text.TextDecoration;
import myworld.obsidian.text.TextShadow;
import myworld.obsidian.text.TextStyle;
import myworld.obsidian.util.LogUtil;


import java.util.*;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.INFO;

public class Renderer implements AutoCloseable {

    private static final Logger log = LogUtil.loggerFor(Renderer.class);

    protected final FontCollection fontCollection;
    protected final TypefaceFontProvider typeProvider;
    protected final Shaper shaper;

    protected UISkin activeSkin;

    protected final Deque<Bounds2D> clipRegions;
    protected final ValueProperty<ColorRGBA> debugColor;
    protected final ValueProperty<Dimension2D> renderDimensions;

    public Renderer(ValueProperty<Dimension2D> dimensions) {
        fontCollection = new FontCollection();
        fontCollection.setDefaultFontManager(FontMgr.getDefault());

        typeProvider = new TypefaceFontProvider();
        fontCollection.setAssetFontManager(typeProvider);
        fontCollection.setEnableFallback(true);

        shaper = Shaper.make(FontMgr.getDefault());

        clipRegions = new ArrayDeque<>();
        debugColor = new ValueProperty<>();
        renderDimensions = dimensions;
    }

    protected void setActiveSkin(UISkin skin) {
        activeSkin = skin;
    }

    public void enterClippingRegion(Bounds2D clipRegion) {
        log.log(FINE, "Entered clipping region {0}", clipRegion);
        clipRegions.push(clipRegion);
    }

    public Bounds2D getCurrentClippingRegion() {
        return clipRegions.isEmpty() ? null : clipRegions.peek();
    }

    public Rect calculateScreenClip() {
        if (clipRegions.isEmpty()) {
            return new Rect(0, 0, renderDimensions.get().width(), renderDimensions.get().height());
        }

        var clip = boundsToRect(clipRegions.peekLast());
        var it = clipRegions.descendingIterator();
        while (it.hasNext()) {
            clip = safeIntersect(clip, boundsToRect(it.next()));
        }
        log.log(FINE, "Calculated screen clip as {0}", clip);
        return clip;
    }

    public void exitClippingRegion() {
        var region = clipRegions.pop();
        log.log(FINE, "Exiting clipping region {0}", region);
    }

    public ValueProperty<ColorRGBA> debugBoundsColor() {
        return debugColor;
    }

    public void registerFont(Typeface typeface) {
        typeProvider.registerTypeface(typeface);
    }


    public void renderDebug(Canvas canvas, Bounds2D componentBounds) {
        if (debugColor.get() != null) {
            var boundingRect = new Rect(componentBounds.left() - 0.5f, componentBounds.top() - 0.5f, componentBounds.right() - 0.5f, componentBounds.bottom() - 0.5f);
            var debugPaint = new Paint();
            debugPaint.setColor(debugColor.get().toARGB());
            debugPaint.setStroke(true);
            debugPaint.setStrokeWidth(1f);
            log.log(FINE, "Rendering debug shape {0}", boundingRect);
            canvas.drawRect(boundingRect, debugPaint);
        }
    }

    public void render(Canvas canvas, Bounds2D componentBounds, StyleClass style, Variables renderVars, StyleLookup styles) {
        try {

            log.log(FINE, "Saving canvas for style render");
            canvas.save();

            renderDebug(canvas, componentBounds);


            var boundingRect = new Rect(componentBounds.left() - 0.5f, componentBounds.top() - 0.5f, componentBounds.right() - 0.5f, componentBounds.bottom() - 0.5f);

            Rect clippingRect = calculateScreenClip();
            log.log(FINE, "Clipping to {0}", clippingRect);
            canvas.clipRect(clippingRect, true);

            var geometry = (Object) createSkiaGeometry(boundingRect, style, renderVars, styles);

            Object fill = getFill(style, renderVars);
            Paint stroke = getStroke(style, renderVars);
            stroke.setAntiAlias(true);

            Move position = style.rule(StyleRules.POSITION, renderVars);
            Rotate rotation = style.rule(StyleRules.ROTATION, renderVars);

            Matrix33 transform = Matrix33.IDENTITY;
            if (rotation != null) {
                transform = Matrix33.makeRotate(-rotation.angle(),
                                new Point(componentBounds.left() + componentBounds.width() / 2f,
                                        componentBounds.top() + componentBounds.height() / 2f))
                        .makeConcat(transform);
            }

            if (position != null) {
                transform = Matrix33.makeTranslate(
                                // The shift by 0.5f aligns pixels so that they make a crisp line rather than a blurred one
                                position.x().toPixels(componentBounds.width()) - 0.5f,
                                position.y().toPixels(componentBounds.height()) - 0.5f)
                        .makeConcat(transform);
            }

            if (geometry instanceof Path p) {
                Path renderPath = new Path();

                renderPath.addPath(p);

                renderPath.transform(transform);

                var visualBounds = renderPath.getBounds();
                if (visualBounds.getWidth() > boundingRect.getWidth() || visualBounds.getHeight() > boundingRect.getHeight()) {

                    switch (style.rule(StyleRules.OVERFLOW_MODE, renderVars, OverflowModes.SCALE)) {
                        case OverflowModes.CLIP -> {
                            var overflowClip = safeIntersect(clippingRect, boundingRect);
                            log.log(FINE, "Overwriting default clip for overflow mode CLIP to {0}", overflowClip);
                            canvas.clipRect(overflowClip, true);
                        }

                        case OverflowModes.SCALE_UNIFORM -> transform = Matrix33.IDENTITY
                                .makeConcat(Matrix33.makeTranslate(visualBounds.getLeft(), visualBounds.getTop()))
                                .makeConcat(Matrix33.makeScale(
                                        Math.min(
                                                boundingRect.getWidth() / visualBounds.getWidth(),
                                                boundingRect.getHeight() / visualBounds.getHeight()
                                        )))
                                .makeConcat(Matrix33.makeTranslate(-visualBounds.getLeft(), -visualBounds.getTop()));
                        case OverflowModes.SCALE -> transform = Matrix33.IDENTITY
                                .makeConcat(Matrix33.makeTranslate(visualBounds.getLeft(), visualBounds.getTop()))
                                .makeConcat(Matrix33.makeScale(
                                        boundingRect.getWidth() / visualBounds.getWidth(),
                                        boundingRect.getHeight() / visualBounds.getHeight()))
                                .makeConcat(Matrix33.makeTranslate(-visualBounds.getLeft(), -visualBounds.getTop()));
                    }

                    renderPath.transform(transform);
                }

                if (fill != null) {
                    if (fill instanceof ColorRGBA paintFill) {
                        Paint paint = new Paint();
                        paint.setColor(paintFill.toARGB());
                        paint.setAntiAlias(true);
                        canvas.drawPath(renderPath, paint);
                    } else if (fill instanceof ResourceHandle handle) {
                        log.log(FINE, "Overwriting clipping to render shape for {0} resource {1} rendering",
                                new Object[]{handle.type(), handle.path()});
                        canvas.clipPath(renderPath, true);
                        if (handle.type().equals(ResourceHandle.Type.SVG)) {
                            var svg = activeSkin.getCachedSvg(handle.path());
                            if (svg != null) {
                                renderSvg(canvas, svg, visualBounds);
                            }
                        } else if (handle.type().equals(ResourceHandle.Type.IMAGE)) {
                            var image = activeSkin.getCachedImage(handle.path());
                            if (image != null) {
                                renderImage(canvas, image, visualBounds);
                            }
                        }
                    } else if (fill instanceof ObsidianSvg svgFill) {
                        log.log(FINE, "Overwriting clipping to render shape for SVG rendering");
                        canvas.clipPath(renderPath, true);
                        renderSvg(canvas, svgFill, visualBounds);
                    } else if (fill instanceof ObsidianImage imageFill) {
                        log.log(FINE, "Overwriting clipping to render shape for image rendering");
                        canvas.clipPath(renderPath, true);
                        renderImage(canvas, imageFill, visualBounds);
                    }
                }

                if (stroke != null) {
                    canvas.drawPath(renderPath, stroke);
                }

            } else if (geometry instanceof RenderableText text) {
                // Note: Because text can specify its own style class,
                // conversion of the high-level Text object to RenderableText
                // performs style class merging. This means that all styleable
                // properties used here must be assigned to the RenderableText
                // during creation, and none of the style variables (such as fill color)
                // available from the outer scopes here may be used.
                try{
                    log.log(FINE, "Saving canvas for text render");
                    var textClip = safeIntersect(clippingRect, boundingRect);
                    log.log(FINE, "Overwriting clipping for rendering text: {0}", textClip);
                    canvas.saveLayer(textClip, null);

                    var textColor = new Paint();
                    textColor.setColor(text.color().toARGB());

                    if (text.backgroundColor() != null) {
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

                    if (text.shadows() != null) {
                        var shadowPaint = new Paint();
                        for (var shadow : text.shadows()) {
                            shadowPaint.setColor(shadow.color().toARGB());
                            shadowPaint.setMaskFilter(MaskFilter.makeBlur(FilterBlurMode.NORMAL, shadow.blurSigma(), false));
                            renderDecoratedText(canvas, text.text(), text.decorations(),
                                    shadow.offset().x().toPixels(boundingRect.getWidth()) + boundingRect.getLeft(),
                                    shadow.offset().y().toPixels(boundingRect.getHeight()) + boundingRect.getTop(),
                                    shadowPaint);
                        }
                    }

                    renderDecoratedText(canvas, text.text(), text.decorations(), text.bounds().getLeft(), text.bounds().getTop(), textColor);
                } finally {
                    log.log(FINE, "Restoring canvas after text render");
                    canvas.restore();
                }
            }

        } catch (Exception e) {
            log.log(INFO, "Error rendering style", e);
        } finally {
            log.log(FINE, "Restoring canvas after style render");
            canvas.restore();
        }
    }

    protected void renderDecoratedText(Canvas canvas, TextBlob text, TextDecoration decor, float x, float y, Paint fill) {
        canvas.drawTextBlob(text, x, y, fill);

        if (decor != null) {
            if (decor.underline()) {
                float underlineY = Math.round(y + text.getTightBounds().getBottom()) - 0.5f;
                canvas.drawLine(x, underlineY, x + text.getBlockBounds().getWidth(), underlineY, fill);
            }
            if (decor.strikethrough()) {
                float strikeY = Math.round(y + (text.getTightBounds().getTop() + text.getTightBounds().getBottom()) / 2.0f) - 0.5f;
                canvas.drawLine(x, strikeY, x + text.getBlockBounds().getWidth(), strikeY, fill);
            }
        }

    }

    protected void renderSvg(Canvas canvas, ObsidianSvg svg, Rect bounds) {
        if (svg.getDom() != null) {
            try (var svgRoot = svg.getDom().getRoot()) {
                log.log(FINE, "Saving canvas for svg render");
                canvas.save();
                canvas.resetMatrix();
                canvas.translate(bounds.getLeft(), bounds.getTop());

                // Scale to stretch SVG to fit the given bounds
                var svgBounds = new Point(bounds.getWidth(), bounds.getHeight());
                var lengthContext = new SVGLengthContext(svgBounds);

                var svgWidth = lengthContext.resolve(svgRoot.getWidth(), SVGLengthType.HORIZONTAL);
                var svgHeight = lengthContext.resolve(svgRoot.getHeight(), SVGLengthType.VERTICAL);

                // Offset by 1 to fit the clipping rectangle
                var hScale = (bounds.getWidth() - 1) / svgWidth;
                var vScale = (bounds.getHeight() - 1) / svgHeight;

                canvas.scale(hScale, vScale);

                svg.getDom().setContainerSize(bounds.getWidth(), bounds.getHeight());
                svg.getDom().render(canvas);

            }finally{
                log.log(FINE, "Restoring canvas after svg render");
                canvas.restore();
            }
        }
    }

    protected void renderImage(Canvas canvas, ObsidianImage image, Rect bounds) {
        try{
            log.log(FINE, "Saving canvas for image render");
            canvas.save();
            canvas.resetMatrix();
            canvas.translate(bounds.getLeft(), bounds.getTop());
            canvas.scale((bounds.getWidth() - 1) / image.width(),
                    (bounds.getHeight() - 1) / image.height());

            canvas.drawImage(image.getImage(), 0, 0);
        }finally{
            log.log(FINE, "Restoring canvas after image render");
            canvas.restore();
        }

    }

    public Object createSkiaGeometry(Rect boundingRect, StyleClass style, Variables renderVars, StyleLookup styles) {
        Object geometry = style.rule(StyleRules.GEOMETRY, renderVars);
        var path = new Path();
        if (geometry instanceof Rectangle r) {
            path.addRect(
                    new Rect(
                            boundingRect.getLeft(),
                            boundingRect.getTop(),
                            boundingRect.getLeft() + pixelWidth(r.width(), boundingRect),
                            boundingRect.getTop() + pixelHeight(r.height(), boundingRect)
                    ));
        } else if (geometry instanceof RoundedRectangle r) {
            path.addRRect(
                    RRect.makeNinePatchLTRB(
                            boundingRect.getLeft(),
                            boundingRect.getTop(),
                            boundingRect.getLeft() + pixelWidth(r.rectangle().width(), boundingRect),
                            boundingRect.getTop() + pixelHeight(r.rectangle().height(), boundingRect),

                            // Radii
                            pixelWidth(r.leftRadius(), boundingRect),
                            pixelHeight(r.topRadius(), boundingRect),
                            pixelWidth(r.rightRadius(), boundingRect),
                            pixelHeight(r.bottomRadius(), boundingRect)
                    ));
        } else if (geometry instanceof Ellipse ellipse) {
            path.addOval(
                    new Rect(
                            boundingRect.getLeft(),
                            boundingRect.getTop(),
                            boundingRect.getLeft() + pixelWidth(ellipse.width(), boundingRect),
                            boundingRect.getTop() + pixelHeight(ellipse.height(), boundingRect)
                    ));
        } else if (geometry instanceof SvgPath svg) {
            var svgPath = Path.makeFromSVGString(svg.interpolate(boundingRect.getWidth(), boundingRect.getHeight()))
                    .transform(Matrix33.makeTranslate(boundingRect.getLeft(), boundingRect.getTop()));
            if (svgPath.isValid()) {
                path.addPath(svgPath);
            } else {
                path.addRect(boundingRect);
            }
        } else if (geometry instanceof String varName) {
            Text text = renderVars.get(varName, Text.class);

            if (text.hasStyle()) {
                style = StyleClass.merge(text.style(), style);
            }

            var font = getFont(style, renderVars);

            TextBlob blob = shaper.shape(text.text(), font, boundingRect.getWidth() + 1);

            var color = style.rule(StyleRules.COLOR, renderVars, Colors.BLACK);
            ColorRGBA backgroundColor = null;
            if (style.hasRule(StyleRules.TEXT_BACKGROUND_COLOR)) {
                backgroundColor = style.rule(StyleRules.TEXT_BACKGROUND_COLOR, renderVars);
            }

            return new RenderableText(blob, font, boundingRect, color, backgroundColor, getTextDecoration(style, renderVars), getShadows(style, renderVars));

        } else {
            // Default to filling in the componentBounds as a rectangle
            path.addRect(boundingRect);
        }

        return path;
    }

    public Font getFont(StyleClass style) {
        return getFont(style, Variables.empty());
    }

    public Font getFont(StyleClass style, Variables renderVars) {
        return getFont(style.rule(StyleRules.FONT_FAMILY, renderVars), getFontStyle(style, renderVars), getFontSize(style, renderVars));
    }

    public Font getFont(String family, TextStyle style, float size) {
        return getFont(family, getFontStyle(style), size);
    }

    public Font getFont(String family, FontStyle style, float size) {
        Typeface typeface;
        if (style != null) {
            Typeface[] typefaces = fontCollection.findTypefaces(new String[]{family}, style);
            typeface = typefaces != null && typefaces.length > 0 ? typefaces[0] : fontCollection.defaultFallback();
        } else {
            typeface = fontCollection.defaultFallback();
        }

        var font = new Font(typeface);
        font.setEdging(FontEdging.SUBPIXEL_ANTI_ALIAS);
        font.setHinting(FontHinting.NORMAL);
        font.setSize(size);
        font.setSubpixel(true);
        return font;
    }

    public TextRuler getTextRuler(String fontFamily, TextStyle style, float size) {
        return new TextRuler(this, getFont(fontFamily, style, size));
    }

    public TextRuler getTextRuler(StyleClass textStyle, Variables v) {
        return new TextRuler(this, getFont(textStyle, v));
    }

    protected Rect boundsToRect(Bounds2D bounds) {
        return new Rect(bounds.left() - 0.5f, bounds.top() - 0.5f, bounds.right() - 0.5f, bounds.bottom() - 0.5f);
    }

    protected Rect safeIntersect(Rect a, Rect b) {
        var i = a.intersect(b);
        return i != null ? i : a;
    }

    public static PaintStrokeCap skiaCap(String cap) {
        return switch (cap) {
            case Borders.CAP_BUTT -> PaintStrokeCap.BUTT;
            case Borders.CAP_ROUND -> PaintStrokeCap.ROUND;
            case Borders.CAP_SQUARE -> PaintStrokeCap.SQUARE;
            default -> PaintStrokeCap.SQUARE;
        };
    }

    public static PaintStrokeJoin skiaJoin(String join) {
        return switch (join) {
            case Borders.JOIN_BEVEL -> PaintStrokeJoin.BEVEL;
            case Borders.JOIN_ROUND -> PaintStrokeJoin.ROUND;
            case Borders.JOIN_MITER -> PaintStrokeJoin.MITER;
            default -> PaintStrokeJoin.BEVEL;
        };
    }

    protected static float pixelWidth(Distance distance, Rect bounds) {
        return distance.toPixels(bounds.getWidth());
    }

    protected static float pixelHeight(Distance distance, Rect bounds) {
        return distance.toPixels(bounds.getHeight());
    }

    protected static float getFontSize(StyleClass style, Variables renderVars) {
        Number size = style.rule(StyleRules.FONT_SIZE, renderVars, 12f);
        return size.floatValue();
    }

    protected static Object getFill(StyleClass style, Variables renderVars) {

        if (style.hasRule(StyleRules.COLOR)) {
            return style.rule(StyleRules.COLOR, renderVars);
        }

        return Colors.WHITE;
    }

    protected static Paint getStroke(StyleClass style, Variables renderVars) {
        Paint stroke = null;
        if (style.hasAny(StyleRules.BORDER_CAP, StyleRules.BORDER_COLOR, StyleRules.BORDER_JOIN,
                StyleRules.BORDER_MITER, StyleRules.BORDER_WIDTH)) {

            Number width = style.rule(StyleRules.BORDER_WIDTH, renderVars, 1f);
            Number miter = style.rule(StyleRules.BORDER_MITER, renderVars, 0f);

            stroke = new Paint()
                    .setStroke(true)
                    .setColor(style.rule(StyleRules.BORDER_COLOR, renderVars, Colors.TRANSPARENT).toARGB())
                    .setStrokeWidth(width.floatValue())
                    .setStrokeCap(skiaCap(style.rule(StyleRules.BORDER_CAP, renderVars, Borders.CAP_SQUARE)))
                    .setStrokeJoin(skiaJoin(style.rule(StyleRules.BORDER_JOIN, renderVars, Borders.JOIN_BEVEL)))
                    .setStrokeMiter(miter.floatValue());
        }
        return stroke;
    }

    protected static FontStyle getFontStyle(StyleClass style, Variables renderVars) {
        var value = style.rule(StyleRules.FONT_STYLE, renderVars);
        if (value == null) {
            value = TextStyle.NORMAL;
        }
        if (value instanceof String s) {
            return getFontStyle(TextStyle.valueOf(s));
        }
        return getFontStyle((TextStyle) value);
    }

    protected static FontStyle getFontStyle(TextStyle style) {
        return switch (style) {
            case NORMAL -> FontStyle.NORMAL;
            case BOLD -> FontStyle.BOLD;
            case ITALIC -> FontStyle.ITALIC;
            case BOLD_ITALIC -> FontStyle.BOLD_ITALIC;
        };
    }

    protected static TextDecoration getTextDecoration(StyleClass style, Variables renderVars) {
        return style.rule(StyleRules.TEXT_DECORATION, renderVars);
    }

    @SuppressWarnings("unchecked")
    protected static TextShadow[] getShadows(StyleClass style, Variables renderVars) {
        if (style.hasRule(StyleRules.TEXT_SHADOW)) {
            var shadows = style.rule(StyleRules.TEXT_SHADOW, renderVars);
            if (shadows instanceof List shadowList) {
                return ((List<TextShadow>) shadowList).toArray(new TextShadow[]{});
            } else if (shadows instanceof TextShadow shadow) {
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
    }
}
