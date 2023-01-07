/*
 *    Copyright 2022 MyWorld, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package myworld.obsidian.layout;

import myworld.obsidian.ObsidianUI;
import myworld.obsidian.geometry.Bounds2D;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.geometry.Point2D;
import myworld.obsidian.properties.ListChangeListener;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.scene.Component;

import java.util.function.Consumer;

import static org.lwjgl.util.yoga.Yoga.*;
import static org.lwjgl.util.yoga.Yoga.YGNodeStyleSetMaxHeight;

public class LayoutEngine {

    // We use Float.NaN since Yoga defines YGUndefined as Float.NaN - this
    // gives API consistency in case we directly return values from Yoga that
    // are undefined.
    public static final float LAYOUT_UNDEFINED = Float.NaN;

    protected final ObsidianUI ui;

    protected final ListChangeListener<Component> listener;

    public LayoutEngine(ObsidianUI ui){
        this.ui = ui;
        listener = this::onComponentChange;
        ui.getRoot().children().addListener(this::onComponentChange);
    }

    public void enable(){
        ui.getRoot().children().addListener(listener);
    }

    public void disable(){
        ui.getRoot().children().removeListener(listener);
    }

    public void layout(){

        var dimensions = ui.getDisplay().getDimensions().get();
        var fWidth = dimensions.width();
        var fHeight = dimensions.height();

        var root = ui.getRoot();
        root.apply(c -> c.preLayouts().forEach(Runnable::run));
        root.layout().clampedSize(Distance.pixels(fWidth), Distance.pixels(fHeight));
        syncLayoutProperties(root);

        var yogaTag = root.getTag(YogaTag.class);
        if(yogaTag != null){
            YGNodeCalculateLayout(yogaTag.node(), fWidth, fHeight, YGDirectionLTR);
        }
    }

    public float getWidth(Component component){
        var tag = component.getTag(YogaTag.class);
        if(tag != null){
            return YGNodeLayoutGetWidth(tag.node());
        }
        return LAYOUT_UNDEFINED;
    }

    public float getHeight(Component component){
        var tag = component.getTag(YogaTag.class);
        if(tag != null){
            return YGNodeLayoutGetHeight(tag.node());
        }
        return LAYOUT_UNDEFINED;
    }

    public Bounds2D getLocalBounds(Component component){
        var tag = component.getTag(YogaTag.class);
        if(tag != null){
            return new Bounds2D(
                    new Point2D(
                            YGNodeLayoutGetLeft(tag.node()),
                            YGNodeLayoutGetTop(tag.node())),
                    YGNodeLayoutGetWidth(tag.node()),
                    YGNodeLayoutGetHeight(tag.node())
            );
        }
        return Bounds2D.UNDEFINED;
    }

    public Bounds2D getSceneBounds(Component component){
        var bounds = getLocalBounds(component);
        float x = bounds.origin().x();
        float y = bounds.origin().y();
        if(component.hasParent()){
            var parentBounds = getSceneBounds(component.getParent());
            x += parentBounds.left();
            y += parentBounds.top();
        }
        return new Bounds2D(new Point2D(x, y), bounds.width(), bounds.height());
    }

    public boolean testLocalBounds(Component component, int x, int y){
        var bounds = getLocalBounds(component);
        return bounds.left() <= x && bounds.right() >= x && bounds.bottom() >= y && bounds.top() <= y;
    }

    public boolean testSceneBounds(Component component, int x, int y){
        var bounds = getSceneBounds(component);
        return bounds.left() <= x && bounds.right() >= x && bounds.bottom() >= y && bounds.top() <= y;
    }

    public void registerRoot(Component component){
        addLayout(component);
    }

    public void unregisterRoot(Component root){
        removeLayout(root);
    }

    protected void onComponentChange(ListProperty<Component> children, int index, Component oldValue, Component newValue){
        if(oldValue != null && newValue == null){
            removeLayout(oldValue);
        }else if(oldValue == null && newValue != null){
            addLayout(newValue);
        }
    }

    protected void addLayout(Component component){
        component.children().addListener(listener);
        var tag = new YogaTag(YGNodeNew());
        component.tag(tag);

        if(component.hasParent()){
            var parent = component.getParent().getTag(YogaTag.class);
            YGNodeInsertChild(parent.node(), tag.node(), YGNodeGetChildCount(parent.node()));
        }

        component.children().forEach(this::addLayout);
    }

    protected void removeLayout(Component component){
        component.children().removeListener(listener);
        component.children().forEach(this::removeLayout);

        var tag = component.getTag(YogaTag.class);
        if(component.hasParent()){
            var parent = component.getParent().getTag(YogaTag.class);
            YGNodeRemoveChild(parent.node(), tag.node());
        }

        YGNodeFree(tag.node());
        component.removeTag(YogaTag.class);
    }

    protected void ensureYogaNode(Component component){
        if(component.layout().node().get() == null){
            long node = YGNodeNew();
            component.layout().node().set(node);
            component.children().forEach(child -> {
                YGNodeInsertChild(node, child.layout().node().get(), YGNodeGetChildCount(node));
            });
        }
    }

    protected void syncLayoutProperties(Component component){
        var yogaTag = component.getTag(YogaTag.class);
        if(yogaTag != null){
            var node = yogaTag.node();
            var layout = component.layout();

            YGNodeStyleSetPositionType(node,
                    switch (layout.positionType().get()) {
                        case ABSOLUTE -> YGPositionTypeAbsolute;
                        case RELATIVE -> YGPositionTypeRelative;
                    });

            YGNodeStyleSetFlexDirection(node,
                    switch(layout.flexDirection().get()){
                        case ROW -> YGFlexDirectionRow;
                        case COLUMN -> YGFlexDirectionColumn;
                        case ROW_REVERSE -> YGFlexDirectionRowReverse;
                        case COLUMN_REVERSE -> YGFlexDirectionColumnReverse;
                    });

            YGNodeStyleSetFlexWrap(node,
                    switch(layout.flexWrap().get()){
                        case WRAP -> YGWrapWrap;
                        case NO_WRAP -> YGWrapNoWrap;
                        case WRAP_REVERSE -> YGWrapReverse;
                    });

            YGNodeStyleSetFlexGrow(node, layout.flexGrow().get());
            YGNodeStyleSetFlexShrink(node, layout.flexShrink().get());

            setDimension(layout.flexBasis().get(),
                    () -> YGNodeStyleSetFlexBasisAuto(node),
                    (b) -> YGNodeStyleSetFlexBasis(node, b),
                    (b) -> YGNodeStyleSetFlexBasisPercent(node, b));

            YGNodeStyleSetAlignContent(node, yogaAlignment(layout.alignContent().get()));
            YGNodeStyleSetAlignSelf(node, yogaAlignment(layout.alignSelf().get()));
            YGNodeStyleSetAlignItems(node, yogaAlignment(layout.alignItems().get()));

            YGNodeStyleSetAspectRatio(node, layout.aspectRatio().get());

            YGNodeStyleSetJustifyContent(node,
                    switch (layout.justifyContent().get()){
                        case FLEX_START -> YGJustifyFlexStart;
                        case FLEX_END -> YGJustifyFlexEnd;
                        case CENTER -> YGJustifyCenter;
                        case SPACE_BETWEEN -> YGJustifySpaceBetween;
                        case SPACE_AROUND -> YGJustifySpaceAround;
                        case SPACE_EVENLY -> YGJustifySpaceEvenly;
                    });

            YGNodeStyleSetDirection(node,
                    switch (layout.layoutDirection().get()){
                        case LTR -> YGDirectionLTR;
                        case RTL -> YGDirectionRTL;
                        case INHERIT -> YGDirectionInherit;
                    });

            var margin = layout.margin().get();
            yogaSetMargin(node, YGEdgeLeft, margin.left());
            yogaSetMargin(node, YGEdgeTop, margin.top());
            yogaSetMargin(node, YGEdgeRight, margin.right());
            yogaSetMargin(node, YGEdgeBottom, margin.bottom());

            var padding = layout.padding().get();
            yogaSetPadding(node, YGEdgeLeft, padding.left());
            yogaSetPadding(node, YGEdgeTop, padding.top());
            yogaSetPadding(node, YGEdgeRight, padding.right());
            yogaSetPadding(node, YGEdgeBottom, padding.bottom());

            var offsets = layout.offsets().get();
            if(offsets != null){
                yogaSetOffsets(node, YGEdgeLeft, offsets.left());
                yogaSetOffsets(node, YGEdgeTop, offsets.top());
                yogaSetOffsets(node, YGEdgeRight, offsets.right());
                yogaSetOffsets(node, YGEdgeBottom, offsets.bottom());
            }

            setDimension(layout.preferredWidth().get(),
                    () -> YGNodeStyleSetWidthAuto(node),
                    (w) -> YGNodeStyleSetWidth(node, w),
                    (w) -> YGNodeStyleSetWidthPercent(node, w));

            setDimension(layout.preferredHeight().get(),
                    () -> YGNodeStyleSetHeightAuto(node),
                    (h) -> YGNodeStyleSetHeight(node, h),
                    (h) -> YGNodeStyleSetHeightPercent(node, h));

            setDimension(layout.minWidth().get(),
                    () -> YGNodeStyleSetMinWidth(node, YGUndefined),
                    (w) -> YGNodeStyleSetMinWidth(node, w),
                    (w) -> YGNodeStyleSetMinWidthPercent(node, w));

            setDimension(layout.maxWidth().get(),
                    () -> YGNodeStyleSetMaxWidth(node, YGUndefined),
                    (w) -> YGNodeStyleSetMaxWidth(node, w),
                    (w) -> YGNodeStyleSetMaxWidthPercent(node, w));

            setDimension(layout.minHeight().get(),
                    () -> YGNodeStyleSetMinHeight(node, YGUndefined),
                    (w) -> YGNodeStyleSetMinHeight(node, w),
                    (w) -> YGNodeStyleSetMinHeightPercent(node, w));

            setDimension(layout.maxHeight().get(),
                    () -> YGNodeStyleSetMaxHeight(node, YGUndefined),
                    (w) -> YGNodeStyleSetMaxHeight(node, w),
                    (w) -> YGNodeStyleSetMaxHeightPercent(node, w));

            component.children().forEach(this::syncLayoutProperties);
        }
    }

    protected int yogaAlignment(ItemAlignment alignment){
        return switch (alignment){
            case AUTO -> YGAlignAuto;
            case STRETCH -> YGAlignStretch;
            case FLEX_START -> YGAlignFlexStart;
            case FLEX_END -> YGAlignFlexEnd;
            case CENTER -> YGAlignCenter;
            case BASELINE -> YGAlignBaseline;
            case SPACE_AROUND -> YGAlignSpaceAround;
            case SPACE_BETWEEN -> YGAlignSpaceBetween;
        };
    }

    protected void setDimension(Distance dist, Runnable autoSetter, Consumer<Float> pixelSetter, Consumer<Float> percentageSetter){
        if(Layout.AUTO.equals(dist)){
            autoSetter.run();
        }else{
            switch (dist.unit()){
                case PIXELS -> pixelSetter.accept(dist.asFloat());
                case PERCENTAGE -> percentageSetter.accept(dist.asFloat());
            }
        }
    }

    protected void yogaSetMargin(long node, int edge, Distance dist){
        setDimension(dist,
                () -> YGNodeStyleSetMarginAuto(node, edge),
                (d) -> YGNodeStyleSetMargin(node, edge, d),
                (d) -> YGNodeStyleSetMarginPercent(node, edge, d));
    }

    protected void yogaSetPadding(long node, int edge, Distance dist){
        setDimension(dist,
                () -> YGNodeStyleSetPadding(node, edge, 0),
                (d) -> YGNodeStyleSetPadding(node, edge, d),
                (d) -> YGNodeStyleSetPaddingPercent(node, edge, d));
    }

    protected void yogaSetOffsets(long node, int edge, Distance dist){
        setDimension(dist,
                () -> YGNodeStyleSetPosition(node, edge, 0),
                (d) -> YGNodeStyleSetPosition(node, edge, d),
                (d) -> YGNodeStyleSetPositionPercent(node, edge, d)
                );
    }
}
